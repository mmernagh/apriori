JFLAGS = -g -cp .
JC = ecj 
.SUFFIXES: .java .class
.java.class:
				$(JC) $(JFLAGS) $*.java

CLASSES = \
				IgnoreContentHandler.java \
				XMLParser.java \
				ParsingFileVisitor.java \
				Preprocess.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
				$(RM) *.class
