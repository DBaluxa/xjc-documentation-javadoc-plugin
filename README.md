[![Autobuild Status](https://travis-ci.org/Hubbitus/xjc-documentation-annotation-plugin.svg?branch=master)](https://travis-ci.org/DBaluxa/xjc-documentation-javadoc-plugin)

XJC plugin to bring XSD descriptions into JavaDoc of generated classes
==========================================================================

Plugin forked from Hubbitus/xjc-documentation-annotation-plugin, because we does not need annonations, only Javadoc in our Java files.
Although adding special tags into the XSD could be another way, to have the XSD documentation as Javadoc in the Java files, it requires XSD files to be changed, which wasn't an option for us.

## What it does: \<annotation>\<documentation> -> Java class JavaDoc

Said we have this object described in XSD:

```xml
  <xs:complexType name="Customer">
    <xs:annotation>
      <xs:documentation>Customer basic data</xs:documentation>
      </xs:annotation>
    <xs:sequence>
      <xs:element name="name" type="xs:string">
        <xs:annotation>
          <xs:documentation>Name of the customer</xs:documentation>
        </xs:annotation>
      </xs:element>
    </xs:sequence>
  </xs:complexType>
```

We run xjc like:

    xjc -npa -no-header -d src/main/generated-java/ -p xsd.generated scheme.xsd

And got class like (getters, setters and any annotations omitted for simplicity):

```java
public class Customer {
  @XmlElement(required = true)
  protected String name;
}
```

**But in my case I want known how to class and fields was documented in source file!**
So it what this plugin do!

So you get:

```java
/**
* Customer basic data
*/
public class Customer {

    /**
     ** Name of the customer
    */
    @XmlElement(required = true)
    protected String name;
}
```

## How to use

### Manual call in commandline
If you want run it manually ensure jar class with plugin in run classpath and just add option `-XPluginDescriptionJavadoc`. F.e.:

    xjc -npa -no-header -d src/main/generated-java/ -p xsd.generated -XPluginDescriptionJavadoc scheme.xsd

### Call from Java/Groovy
```groovy
  Driver.run(
    [
       '-XPluginDescriptionJavadoc'
        ,'-d', generatedClassesDir.absolutePath
        ,'-p', 'info.dbaluxa.generated.test'
        ,'Example.xsd'
    ] as String[]
    ,new XJCListener() {...}
  )
```

See test [XJCPluginDescriptionJavadocTest](src/test/groovy/info/hubbitus/XJCPluginDescriptionJavadocTest.groovy) for example.

### Use from Gradle

With [gradle-xjc-plugin](https://github.com/unbroken-dome/gradle-xjc-plugin):

```gradle
plugins {
  id 'java'
  id 'org.unbroken-dome.xjc' version '1.4.1' // https://github.com/unbroken-dome/gradle-xjc-plugin
}

...

dependencies {
  xjcClasspath 'info.dbaluxa:xjc-documentation-javadoc-plugin:1.0'
}

// Results by default in `build/xjc/generated-sources`
xjcGenerate {
  source = fileTree('src/main/resources') { include '*.xsd' }
  packageLevelAnnotations = false
  targetPackage = 'info.dbaluxa.xjc.plugin.example'
  extraArgs = [ '-XPluginDescriptionJavadoc' ]
}
```
Just run:

    ./gradlew xjcGenerate

## Development:

Build:

    ./gradlew jar

Run tests:

    ./gradlew test

## Licensed under MIT
