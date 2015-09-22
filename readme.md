JStringes
========

##What is a *stringe*?!

The *Stringe* is a wrapper for the Java String object that tracks line, column, offset, and other metadata for substrings.

Stringes were originally dreamt up by TheBerkin in C# - [check it out here](https://github.com/TheBerkin/Stringes). It's much more feature-filled than this port currently is.

Stringes can be created from normal strings, either through the constructor or using `Stringe.toStringe()`. There is no operator overloading in Java, so we don't have the luxury of simply assigning a String to a Stringe.
```java
Stringe stringeA = new Stringe("Hello\nWorld!");
Stringe stringeB = Stringe.toStringe("Hello\nWorld!");
```

###Support for native String methods

The `Stringe` class supports many of the same fabulous methods that regular strings have.
Unlike the String type, however, methods like `Stringe.split` return a `List<Stringe>` instead of an array.
```java
List<Stringe> lines = stringe.split('\n');
List<Stringe> words = stringe.split(' ');
```

###Finding the parent string from a substringe

Each *substringe* can be traced back to the string it originally came from.
```java
Stringe parent = new Stringe("The quick brown fox jumps over the lazy dog");
Stringe substr = parent.substringe(16, 3); // "fox"
System.out.println(substr.getParent()); // "The quick brown fox jumps over the lazy dog"
```

###Location tracking

Substringes keep track of the line, column, and index on which they appear. This information can be easily accessed through properties. This is **especially** useful when writing lexers, so that errors in compiled code can be traced back to the exact place where the associated tokens were read.

```java
List<Stringe> lines = new Stringe("Hello\nWorld!").split('\n');
for(Stringe substringe : lines) {
    System.out.println("Line {0}: {1}", substringe.getLine(), substringe.getValue());
}
```
```
Line 1: Hello
Line 2: World!
```

###Ranges

In some instances, such as when working with tokens, retrieving a range of text between two elements in the parent string can yield extremely useful data. This is made possible in Stringes through two methods: `Stringe.between()` and `Stringe.range()`.

The `Stringe.range()` method returns a substringe whose endpoints comprise the two `Stringe` objects passed to it and any text between them:
```java
Stringe parent = new Stringe("The quick brown fox jumps over the lazy dog");
Stringe a = parent.substringe(0, 3); // "The"
Stringe b = parent.substringe(16, 3); // "fox"
System.out.println(Stringe.range(a, b)); // "The quick brown fox"
```

The `Stringe.between` method returns a substringe comprised of all the text between the two `Stringe` objects passed to it:
```java
Stringe parent = new Stringe("Here are (some words) in parentheses.");
Stringe a = parent.substringe(9, 1); // "("
Stringe b = parent.substringe(20, 1); // ")"
System.out.println(Stringe.between(a, b)); // "some words"
```
