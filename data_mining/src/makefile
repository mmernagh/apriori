# makefile for lab1 of CSE 5243
# Author: mernagh
JFLAGS = -cp lucene-core-4.10.0.jar:lucene-analyzers-common-4.10.0.jar:.
JC = ecj 
JVM = java
OUTPUT=feature_vectors.txt
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
		$(JVM) $(JFLAGS) $(MAIN) > $(OUTPUT)

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class
		$(RM) $(OUTPUT)

