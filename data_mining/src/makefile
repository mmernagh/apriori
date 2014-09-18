JFLAGS = -cp lucene-core-4.10.0.jar:lucene-analyzers-common-4.10.0.jar:. 
JC = ecj 
JVM = java
.SUFFIXES: .java .class
.java.class:
				$(JC) $(JFLAGS) $*.java

CLASSES = \
				IgnoreContentHandler.java \
        ArticleData.java \
        AddContentHandler.java \
				ParsingFileVisitor.java \
				XMLParser.java \
				Preprocess.java

MAIN = Preprocess 

run: classes
		$(JVM) $(MAIN)

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class

