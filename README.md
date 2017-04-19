# webrtc-signaling-server [![Build Status](https://travis-ci.org/soriole/webrtc-signaling-server.svg?branch=master)](https://travis-ci.org/soriole/webrtc-signaling-server)
Provides signaling mechanism to make audio and video calls using WebRTC.

Cloned from NextRTC Signaling Server: https://github.com/mslosarz/nextrtc-signaling-server

Adds additional signals including:
1. Calling someone
2. Accepting calls
3. Rejecting calls

### Use it:

#### Gradle:

```
allprojects{
  repositories{
    maven { 
      url 'https://jitpack.io' 
    }
  }
}
```

```
dependencies { 
  compile 'com.github.soriole:webrtc-signaling-server:v0.0.6' 
}
```
#### Maven:
```
<repositories>
  <repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
  </repository>
</repositories>
```

```
<dependency>
  <groupId>com.github.soriole</groupId>
  <artifactId>webrtc-signaling-server</artifactId>
  <version>v0.0.6</version>
</dependency>
```
