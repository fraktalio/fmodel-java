# **f`(`model`)`** - Functional and Reactive Domain Modeling with Java

When you’re developing an information system to automate the activities of the business, you are modeling the business.
The abstractions that you design, the behaviors that you implement, and the UI interactions that you build all reflect
the business — together,
they constitute the model of the domain.

## `IOR<Library, Inspiration>`

This project can be used as a library, or as an inspiration, or both. **It provides just enough tactical
Domain-Driven Design patterns, optimised for Event Sourcing and CQRS.**

- The `domain` module/package is fully isolated from the application layer and API-related concerns. It represents a
  pure
  declaration of the program logic. It is written in Java programming language, without
  additional
  dependencies.
- The `application` module/package orchestrates the execution of the logic by loading state, executing `domain`
  components
  and storing new state. It is written in Java programming language.

## Experimental

**This project is in experimental phase**, and it is not published to Maven Central.

It is using [Amber **productivity-oriented** features](https://openjdk.org/projects/amber/) to accelerate development of
applications:

- records
- sealed hierarchy
- exhaustive pattern matching for Switch expressions

We plan to
use [Project Loom](https://blogs.oracle.com/javamagazine/post/going-inside-javas-project-loom-and-virtual-threads)
within the Application module/package to enable high-throughput lightweight concurrency and new programming models on
the Java platform.

Please refer to [kotlin](https://github.com/fraktalio/fmodel) or [typescript](https://github.com/fraktalio/fmodel-ts)
production ready versions of the libraries.

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