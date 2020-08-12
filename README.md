# SOCommon
## Common utility classes in Java.

Quite a lot of these classes were written a long time ago (in the 2nd half of the 1990s) and thus, date back to
the initial versions of Java. Apache, Google etc. were not there in those days and free class libraries were not available like today.
That is the reason why you see a lot of classes and methods that are already available now
in many of the standard free packages still exist in this class library too.

However, most of these classes are used in Production environments and 
I have added new classes recently whenever new requirements came up. Whenever
some new methods are added to the existing classes or whenever new features are
added to the existing methods, I tried to revamp the existing code
(and documentation) to use the latest Java features. I know that full-fledged
documentation is missing in many classes but I will be adding it as and when I
get some free time.

### Maven

```
<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/syampillai/SOCommon</url>
    </repository>
</repositories>
```
```
<dependency>
  <groupId>com.storedobject</groupId>
  <artifactId>so-common</artifactId>
  <version>3.2.8</version>
</dependency>
```