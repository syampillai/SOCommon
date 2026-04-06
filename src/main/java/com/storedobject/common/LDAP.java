/*
 * Copyright 2018 Syam Pillai
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.storedobject.common;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import static javax.naming.directory.SearchControls.SUBTREE_SCOPE;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.BasicAttribute;
import javax.naming.ldap.StartTlsResponse;
import javax.naming.ldap.StartTlsRequest;
import javax.net.ssl.*;

/**
 * The {@code LDAP} class provides an interface for interacting with an LDAP server.
 * It allows authentication, user information retrieval, and user management within an LDAP domain.
 * This class is designed to establish a connection to the LDAP server, fetch user data, and
 * modify user attributes such as passwords.
 *
 * @author Syam
 */
public class LDAP {

    private static final String USER_PRINCIPAL_NAME = "userPrincipalName";
    private final String domainName;
    private static final String[] userAttributes = {
            "distinguishedName", "cn", "name", "uid",
            "sn", "givenname", "memberOf", "samaccountname",
            USER_PRINCIPAL_NAME,
    };
    private LdapContext context;

    private static final HostnameVerifier DO_NOT_VERIFY = (hostname, session) -> true;

    private static final TrustManager[] TRUST_ALL_CERTS = new TrustManager[] {

            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
    };

    /**
     * Constructs an LDAP instance for connecting to an LDAP server using the specified credentials
     * and domain name. This constructor assumes no specific server is provided.
     *
     * @param username the username for authentication.
     * @param password the password for authentication.
     * @param domainName the domain name of the LDAP server.
     * @throws NamingException if an error occurs during the LDAP connection setup.
     */
    public LDAP(String username, String password, String domainName) throws NamingException {
        this(username, password, domainName, null);
    }

    /**
     * Constructs an LDAP instance for connecting to an LDAP server using the specified credentials,
     * domain name, and LDAP server address.
     *
     * @param username the username used for authentication.
     * @param password the password associated with the username for authentication.
     * @param domainName the domain name of the LDAP server.
     * @param server the address of the LDAP server. If null, a default server is assumed based on
     *               the domain name.
     * @throws NamingException if an error occurs during the LDAP connection setup.
     */
    public LDAP(String username, String password, String domainName, String server) throws NamingException {
        this.domainName = domainName;
        context = getConnection(username, password, server);
    }

    /**
     * Closes the LDAP connection and releases associated resources.
     *
     * This method checks if the internal LDAP context is initialized and, if so,
     * attempts to close it. Any {@link NamingException} that occurs during closure
     * is caught and ignored.
     *
     * Once the context is closed, the internal reference to it is nullified to
     * prevent further usage.
     */
    public void close() {
        if(context != null) {
            try {
                context.close();
            } catch (NamingException ignored) {
            }
            context = null;
        }
    }

    private LdapContext getConnection(String username, String password, String server) throws NamingException {
        if(password != null){
            password = password.trim();
            if (password.length() == 0) {
                password = null;
            }
        }
        Hashtable<String, String> props = new Hashtable<>();
        String principalName = username + "@" + domainName;
        props.put(Context.SECURITY_PRINCIPAL, principalName);
        if (password != null) {
            props.put(Context.SECURITY_CREDENTIALS, password);
        }
        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        if(server == null) {
            server = "ldap." + domainName;
        }
        props.put(Context.PROVIDER_URL, "ldap://" + server + '/');
        return new InitialLdapContext(props, null);
    }

    /**
     * Retrieves a user by their username. This method searches for the user in the LDAP directory
     * based on the provided username and returns an instance of the {@link User} class if found.
     *
     * @param username the username of the user to retrieve. It may include a domain in the format
     *                 "username@domain" or "domain\\username." If the domain is not provided,
     *                 the domain from the current authenticated context or instance configuration is used.
     * @return an instance of the {@link User} class representing the user if found, or {@code null}
     *         if the user could not be found or an error occurred during the search.
     */
    public User getUser(String username) {
        try{
            String domainName = null;
            int p;
            if ((p = username.indexOf('@')) >= 0 || (p = username.indexOf('\\')) >= 0){
                username = username.substring(0, p);
                domainName = username.substring(p + 1);
            } else {
                String authenticatedUser = (String) context.getEnvironment().get(Context.SECURITY_PRINCIPAL);
                if((p = authenticatedUser.indexOf('@')) >= 0){
                    domainName = authenticatedUser.substring(p + 1);
                }
            }
            if(domainName == null) {
                domainName = this.domainName;
            }
            String principalName = username + "@" + domainName;
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SUBTREE_SCOPE);
            controls.setReturningAttributes(userAttributes);
            NamingEnumeration<SearchResult> answer = context.search( toDC(domainName), "(& (" + USER_PRINCIPAL_NAME + "=" +
                    principalName + ")(objectClass=user))", controls);
            if (answer.hasMore()) {
                Attributes attr = answer.next().getAttributes();
                Attribute user = attr.get(USER_PRINCIPAL_NAME);
                if (user != null) {
                    return new User(attr);
                }
            }
        } catch(NamingException ignored){
        }
        return null;
    }

    /**
     * Retrieves a list of users from the LDAP directory based on the current authenticated
     * user's domain. This method searches for users of the object class "user" within the
     * LDAP directory under the domain derived from the authenticated user's principal.
     *
     * If the authenticated user's principal contains a domain (e.g., "username@domain"),
     * the domain is extracted and used within the query. The search includes attributes
     * defined by the `userAttributes` field of the containing class.
     *
     * @return an {@code ArrayList} of {@link User} instances representing users in the
     *         LDAP directory, or {@code null} if an error occurs during authentication
     *         or directory search.
     */
    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        String authenticatedUser;
        try {
            authenticatedUser = (String) context.getEnvironment().get(Context.SECURITY_PRINCIPAL);
        } catch (NamingException e1) {
            return null;
        }
        if (authenticatedUser.contains("@")){
            String domainName = authenticatedUser.substring(authenticatedUser.indexOf("@")+1);
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SUBTREE_SCOPE);
            controls.setReturningAttributes(userAttributes);
            NamingEnumeration<SearchResult> answer;
            try {
                answer = context.search( toDC(domainName), "(objectClass=user)", controls);
            } catch (NamingException e1) {
                return null;
            }
            try{
                while(answer.hasMore()) {
                    Attributes attr = answer.next().getAttributes();
                    Attribute user = attr.get(USER_PRINCIPAL_NAME);
                    if (user!=null){
                        users.add(new User(attr));
                    }
                }
            } catch(Exception e) {
                return null;
            }
        }
        return users;
    }

    private static String toDC(String domainName) {
        StringBuilder buf = new StringBuilder();
        for (String token : domainName.split("\\.")) {
            if(token.isEmpty()) {
                continue;
            }
            if(buf.length() > 0) {
                buf.append(",");
            }
            buf.append("DC=").append(token);
        }
        return buf.toString();
    }

    /**
     * Represents an LDAP user with attributes fetched from an LDAP directory.
     * This class provides methods to access user properties, convert the user into a
     * string representation, and allows password modification for the user.
     *
     * @author Syam
     */
    public class User {

        private final String distinguishedName;
        private final String userPrincipal;
        private final String commonName;

        /**
         * Constructs a new User instance by extracting values from the provided Attributes object.
         *
         * @param attr the Attributes object containing user attributes, such as user principal name,
         *             common name, and distinguished name. Must not be null, and must include the
         *             required attributes.
         * @throws NamingException if an error occurs while accessing attributes from the
         *                                      provided Attributes object.
         */
        public User(Attributes attr) throws javax.naming.NamingException {
            userPrincipal = (String) attr.get(USER_PRINCIPAL_NAME).get();
            commonName = (String) attr.get("cn").get();
            distinguishedName = (String) attr.get("distinguishedName").get();
        }

        /**
         * Retrieves the user principal name for this user.
         *
         * @return the user principal name as a String.
         */
        public String getUserPrincipal(){
            return userPrincipal;
        }

        /**
         * Retrieves the common name of the user.
         *
         * @return the common name as a String.
         */
        public String getCommonName(){
            return commonName;
        }

        /**
         * Retrieves the distinguished name of the user.
         *
         * @return the distinguished name as a String.
         */
        public String getDistinguishedName(){
            return distinguishedName;
        }

        @Override
        public String toString(){
            return getDistinguishedName();
        }

        /**
         * Updates the password for the user stored in the LDAP directory.
         * This method attempts to change the user's password by first removing the
         * old password and then adding the new password using LDAP modify operations.
         * It establishes an SSL/TLS connection with the LDAP server during the process.
         *
         * @param oldPass the current password of the user. Must not be null or empty.
         * @param newPass the new password to set for the user. Must meet the LDAP server's password policy.
         * @throws Exception if an error occurs during the password change process,
         *                   such as connection issues, SSL configuration errors, or password policy violations.
         */
        public void changePassword(String oldPass, String newPass) throws Exception {
            String dn = getDistinguishedName();
            StartTlsResponse tls;
            try {
                tls = (StartTlsResponse) context.extendedOperation(new StartTlsRequest());
            } catch(Exception e) {
                throw new java.io.IOException("Failed to establish SSL connection to the LDAP server.");
            }
            tls.setHostnameVerifier(DO_NOT_VERIFY);
            SSLSocketFactory sf = null;
            try {
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, TRUST_ALL_CERTS, null);
                sf = sc.getSocketFactory();
            } catch(NoSuchAlgorithmException | KeyManagementException ignored) {
            }
            tls.negotiate(sf);
            try {
                ModificationItem[] modificationItems = new ModificationItem[2];
                modificationItems[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("unicodePwd", getPassword(oldPass)));
                modificationItems[1] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("unicodePwd", getPassword(newPass)) );
                context.modifyAttributes(dn, modificationItems);
            } catch(InvalidAttributeValueException e){
                throw new NamingException("Password policy error");
            } finally {
                tls.close();
            }
        }

        private byte[] getPassword(String newPass){
            String quotedPassword = "\"" + newPass + "\"";
            char[] unicodePwd = quotedPassword.toCharArray();
            byte[] pwdArray = new byte[unicodePwd.length * 2];
            for (int i=0; i<unicodePwd.length; i++) {
                pwdArray[i*2 + 1] = (byte) (unicodePwd[i] >>> 8);
                pwdArray[i * 2] = (byte) (unicodePwd[i] & 0xff);
            }
            return pwdArray;
        }
    }
}