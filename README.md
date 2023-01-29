# **f`(`model`)`** - Functional and Reactive Domain Modeling with Java

When you’re developing an information system to automate the activities of the business, you are modeling the business.
The abstractions that you design, the behaviors that you implement, and the UI interactions that you build all reflect
the business — together,
they constitute the model of the domain.

**This project is in experimental phase**, and it is not published to Maven Central.

It is using [Amber **productivity-oriented** features](https://openjdk.org/projects/amber/) to accelerate development of
applications:

- records
- sealed hierarchy
- exhaustive pattern matching for Switch expressions

Please refer to [kotlin](https://github.com/fraktalio/fmodel) or [typescript](https://github.com/fraktalio/fmodel-ts)
versions (production ready) of the libraries.

## Requirements

- Java 19

## Driven by Maven

```shell
./mvnw clean verify
```

## Examples

Check [tests](src/test/java/com/fraktalio/fmodel/domain/example)

---
Created with :heart: by [Fraktalio](https://fraktalio.com/)