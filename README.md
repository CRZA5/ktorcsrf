# Ktorcsrf

A Ktor feature for protect your site from csrf attacks.

Based on
[CSRF Middleware](https://github.com/gofiber/fiber/tree/master/middleware/csrf)

## Installation

Make sure you have the [JitPack](https://jitpack.io/) repository added to your project and then add the dependency.

### Gradle (Groovy)

```groovy
repositories {
  // Existing entries
  maven { url 'https://jitpack.io' } // JitPack repository
}

dependencies {
  implementation 'com.github.CRZA5:ktorcsrf:-SNAPSHOT'
}
```

### Gradle (Kotlin)

```kotlin
repositories {
  // Existing entries
  maven("https://jitpack.io/") // JitPack repository
}

dependencies {
  implementation("com.github.CRZA5:ktorcsrf:-SNAPSHOT")
}
```

### Maven

```xml

<repositories>
  <!-- Existing entries -->
  <repository> <!-- JitPack repository -->
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

```xml
<dependency>
  <groupId>com.github.CRZA5</groupId>
  <artifactId>ktorcsrf</artifactId>
  <version>-SNAPSHOT</version>
</dependency>
```

## Usage

```kotlin

import dev.crza.ktorcsrf.KtorCsrf
import io.ktor.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "127.0.0.1") {
        install(KtorCsrfFeature){
            // Name of the session cookie. This cookie will store session key.
            // Optional. Default value "csrf_".
            cookieName = "csrf_"

            // Domain of the CSRF cookie.
            // Optional. Default value "".
            cookieDomain = ""

            // Path of the CSRF cookie.
            // Optional. Default value "".
            cookiePath = ""
            
            
            // Indicates if CSRF cookie is secure.
            // Optional. Default value false.
            cookieSecure = false

            // Indicates if CSRF cookie is HTTP only.
            // Optional. Default value false.
            cookieHTTPOnly = false


            // Expiration is the duration before csrf token will expire
            //
            // Optional. Default: 3600000 ms
            expiration = 3600000


            // Secret key generate CSRF token
            // Required
            secretKey = null
        }
    }.start(wait = true)
}
```

Feel free to create an issue if you notice something wrong. Thanks.

You might face issues if you're using Ktor version below `1.5.3`
