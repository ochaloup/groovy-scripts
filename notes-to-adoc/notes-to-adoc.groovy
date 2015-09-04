#!/usr/bin/groovy

// setting logging
import java.util.logging.*
Logger log =  Logger.getLogger("")
log.level = Level.FINE
System.setProperty('java.util.logging.SimpleFormatter.format', '>>> %4$s: %5$s%n')
log.handlers.each {
    if(it instanceof ConsoleHandler){
        it.level = Level.ALL
        it.formatter = new SimpleFormatter()
    }
}

// parsing :) arguments
if(!args || !args[0]?.trim()) {
  println "No argument specified"
  return
}
def outputFile = "out.txt"
if(args.size() > 1 && args[1]?.trim()) {
  outputFile = args[1]
}


// processing
def outF = new File(outputFile)
outF.write("")

def text = ""
new File(args[0]).withReader { r ->
  def line
  def isNewSection = false
  while((line = r.readLine()) != null) {
    log.fine "Line '$line'"
    def titleMatcher = (line =~ /^[ \t]*title:[ \t]*(.*)/)
    if(titleMatcher.size() == 1) {
      log.fine "Title here"
      def title = titleMatcher[0][1]
      outF.append(String.format("%s%n", title)) 
      outF.append(("-" * title.size()) + String.format("%n")) 
    } else {
      if(isNewSection && line =~ /^[a-z]*:/) {
        println ">>> New section detected - writing file"
        outF.append(text)
        text = ""
      }
      isNewSection = false

      if(line.trim().isEmpty()) {
        log.fine "New section expected - let's see in the next run"
        isNewSection = true
      }

      line.replaceAll(/^tag:(.*)/) { all, data -> "icon:tags[]" }
      line.replaceAll(/^source:(.*)/) { all, data -> "icon:bookmark[]" }
      line.replaceAll(/^links:(.*)/) { all, data -> "icon:plus[]" }

      text += line + String.format("%n")
    }
  }
}


outF.append(text)
