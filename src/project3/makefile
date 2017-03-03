JCC = javac

JFLAGS = -g

JRUN = java

PICO = pico

default: Project3.class

#-------------------------------------
Project1.class: Project3.java
	$(JCC) $(JFLAGS) Project3.java
#-------------------------------------
clean:
	$(RM) *.class

run:
	$(JRUN) Project1 input.txt

runout:
        $(JRUN) Project3 input.txt > output.txt

out:	
	$(PICO) output.txt

