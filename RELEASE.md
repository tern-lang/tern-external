https://central.sonatype.org/publish/requirements/#releasing-to-central

set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_181
set Path=C:\Program Files\Java\jdk1.8.0_181\bin;%Path%

# perform build
mvn clean install

# perform release
mvn clean deploy

<!-- mvn clean deploy -P release -->