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

public class LDAP {

    private static final String USER_PRINCIPAL_NAME = "userPrincipalName";
    private String domainName;
    private static String[] userAttributes = {
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

    public LDAP(String username, String password, String domainName) throws NamingException {
        this(username, password, domainName, null);
    }

    public LDAP(String username, String password, String domainName, String server) throws NamingException {
        this.domainName = domainName;
        context = getConnection(username, password, server);
    }

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

    public class User {

        private String distinguishedName;
        private String userPrincipal;
        private String commonName;

        public User(Attributes attr) throws javax.naming.NamingException {
            userPrincipal = (String) attr.get(USER_PRINCIPAL_NAME).get();
            commonName = (String) attr.get("cn").get();
            distinguishedName = (String) attr.get("distinguishedName").get();
        }

        public String getUserPrincipal(){
            return userPrincipal;
        }

        public String getCommonName(){
            return commonName;
        }

        public String getDistinguishedName(){
            return distinguishedName;
        }

        public String toString(){
            return getDistinguishedName();
        }

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