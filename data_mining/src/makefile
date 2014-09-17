JFLAGS = -cp .
JC = ecj
JVM = java
.SUFFIXES: .java .class
.java.class:
				$(JC) $(JFLAGS) $*.java

CLASSES = \
				IgnoreContentHandler.java \
				XMLParser.java \
				ParsingFileVisitor.java \
        ArticleData.java \
				Preprocess.java

MAIN = Preprocess 

run: classes
		$(JVM) $(MAIN)

classes: $(CLASSES:.java=.class)

clean:
		$(RM) *.class

