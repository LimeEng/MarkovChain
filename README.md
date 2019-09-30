![](https://github.com/LimeEng/MarkovChain/workflows/Java%20CI/badge.svg)

# Markov Chain
What is a Markov chain? A great and concise explaination can be found [here](http://setosa.io/ev/markov-chains/), and a longer, more formally correct explaination is available at [Wikipedia](https://en.wikipedia.org/wiki/Markov_chain)

To understand the sample code, the first link is all that is needed.

- [Sample code](#sample-code)
  - [Basic usage](#basic-usage)
  - [Combining chains](#combining-chains)
- [Installation](#installation)

## Sample Code

### Basic Usage

```java
Stream<String> inputStream = ...
MarkovChain<String> chain = new MarkovChain<>(2);
chain.add(inputStream);
chain.stream()
    .limit(200)
    .forEach(System.out::println);
```

It's that simple! The constructed MarkovChain generates an infinite stream which will never terminate. This is possible because the input is seen as circular. Thus, the last element in the stream precedes the first element. 

This particular MarkovChain is of order 2, which means that the last two elements are considered when choosing the next one. In this case, the outputstream starts at a random element, but this can be customized by optionally specifying a starting TokenSequence or a custom RandomGenerator, or both. 

The real challenge, which this project doesn't pretend to solve, (at least not yet) is how the input should be tokenized. That is left as an exercise to the reader. 

Note that the MarkovChain is generic, which means that any kind of object can be used, not just strings! Due to the nature of Markov chains a large input corpus is preferred if an output that deviates from the source is desired. 

### Combining Chains

Markov chains can also be combined!

```java
Stream<String> inputA = ...;
Stream<String> inputB = ...;
        
MarkovChain<String> chainA = new MarkovChain<>(2);
MarkovChain<String> chainB = new MarkovChain<>(2);
        
chainA.add(inputA);
chainB.add(inputB);

MarkovChain<String> merged = MarkovChain.merge(chainA, chainB);
```

The newly constructed Markov chain can be used like any other. It is also possible to optionally specify weights, to place a relative emphasis on each source. This is how that is done:

```java
MarkovChain<String> merged = MarkovChain.merge(Arrays.asList(chainA, chainB), Arrays.asList(1, 2));
```

This means that it's twice as likely that a connection in chainB is chosen instead of chainA. 

To merge two or more Markov chains, three conditions must be fulfilled: 

- No input arguments may be null.
- The length of the input arguments must be the same.
- All Markov chains must be of the same order.

If any of these conditions is not met, an exception will be thrown.

## Installation

This project has a number of dependencies which are managed by Gradle. Download the repository by:
```
git clone git@github.com:LimeEng/MarkovChain.git
```
Build the project and download the required dependencies by running:
```
./gradlew build
```
