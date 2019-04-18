+ Refactored parts:
  + Modifies Template class and added Actions interface
    + Using "Strategy Pattern" to encapsulate the action method into the classes which implement the Actions interface. Such a modification decouples the Template class from implementing concrete actions of the specific template command (e.g. --email and --letter).
    + Defines some action class that implemented Actions interface. Such classes can define special actions of how to deal with the completed text content. (e.g. sending emails for --email, or printing on papers for --letter)
    + Declares a variable of Actions polymorphism type in the Template class. The Template object does not need to know how to act on the completed text content, but just delegate the concrete Actions object to take appropriate actions.
  + Modifies CsvParser class:
    + Add a conditional statement in the "parseValue" method that can check if the current line of the CSV file is valid. If the number of values of the current line does not match the number of the headers, an IncompleteRecordException will be thrown.
    + Add a try/catch block in the "startParse" method. If an IncompleteRecordException is caught, then add the current line into the incomplete list (list with error lines). Otherwise, notifies all the observers (Template objects) to take actions (e.g. generate emails and/or letters).
    + After all lines were read, displays the total number of valid lines. If incomplete lines exist, then displayed each of incomplete lines.
  + Modifies Option, Options and ArgParser class
    + Adds and modifies some attributes to the Option class, which makes the Option class can meet more extra conditions and makes the attributes easier to understand and use.
    + Modifies some collection data structures in the Options class in order to manage the group of option objects more effectively.
    + Adds and improved some check methods in the ArgParser class that can meet more requirement of command line arguments parsing. At the same time, deletes two collection data structures that reduce the space complexity.
  + Modifies MainGenerator class:
    + Renames the constant fields that make easier to understand and modify the constants.
  + Improves JavaDocs
    + Adds more proper comments that explain the function of every class and method.
  + Improves JaCoCo coverages
    + Adds more test codes that improves JaCoCo coverages from (Assignment5: 94%, 85%) to (Assignment5R, 95%, 88%). By the way, I have tried my best to improve it, but the rest percentage of coverages due to IO exception and Main class which I can not use test codes to simulate.