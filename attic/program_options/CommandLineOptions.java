//
//   Copyright 2014-2019 Eduardo R. B. Marques
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package org.cooperari.tools;

import java.io.PrintStream;
import java.util.IdentityHashMap;
import java.util.TreeMap;
/**
 * Program options.
 * 
 * @since 0.2
 */
@ProgramOption(value="d", description="d does something")
@ProgramOption(value="x", description="x does something", requiresArgument=true, argumentId="tOf", defaultValue="false")
public final class CommandLineOptions { 

  /**
   * Enumeration class that represent the possible options.
   */
  private final TreeMap<String,ProgramOption> _options = new TreeMap<>();
  
  /**
   * Option values.
   */
  private IdentityHashMap<String, String> _values = new IdentityHashMap<>();
  
  /**
   * Construct object. 
   * @param programClass Program class.
   */
  CommandLineOptions(Class<?> programClass) {
    ProgramOptions po = programClass.getDeclaredAnnotation(ProgramOptions.class);
    if (po != null) {
      for (ProgramOption o : po.value()) {
        String initial = o.requiresArgument() ? o.defaultValue() : null;
        _options.put(o.value(), o);
        _values.put(o.value(), initial);
       }
    }
  }
  
  /**
   * Test if option is set.
   * @param id Option id.
   * @return <code>true</code> if option is set.
   * @throws CommandLineOptionException if option id is invalid
   */
  public boolean isSet(String id) throws CommandLineOptionException {
    return value(id) != null;
  }
  
  /**
   * Get value for an option, when option requires an argument.
   * @param id Option id.
   * @return Option value.
   * @throws CommandLineOptionException if option id is invalid
   */
  public String value(String id) throws CommandLineOptionException {
    checkOptionIsValid(id);
    return _values.get(id);
  }


  @SuppressWarnings("javadoc")
  private void checkOptionIsValid(String id) throws CommandLineOptionException {
    if (!_values.containsKey(id)) {
      throw new CommandLineOptionException("Invalid option '-" + id + "'");
    } 
  }

  /**
   * Parse command line options. 
   * 
   * <p>The supplied arguments are parsed until an element 
   * that does not start with the <code>'-'</code> character is found.
   * The index of the first array element that is not an option is returned.
   * If all elements are considered options, the array length is returned.
   * </p> 
   * @param args Array of arguments.
   * @throws CommandLineOptionException if option is not recognized or is not well-formed.
   * @return Index of last position analyzed for options.
   */
  public int parse(String... args) throws CommandLineOptionException {
    for (int index=0; index < args.length; index++) {
      String id = args[index];
      if (id.charAt(0) != '-') {
        return index;
      }
      checkOptionIsValid(id);
      ProgramOption option = _options.get(id);
      String optionValue = "";
      if (option.requiresArgument()) {
        index++;
        optionValue = index == args.length || args[index].charAt('0') == '-' ?
            null : args[index];
        if (optionValue == null ) {
          throw new CommandLineOptionException("Expected value for option: '-" + id + "'");
        }
      }
      _values.put(id, optionValue);
    }
    return args.length;
  }
  
  /**
   * Print usage for these command line options.
   * @param programName Program name.
   * @param out Output stream.
   * @param extraArgs Description of program arguments (beyond options).
   */
  public void printUsage(String programName, PrintStream out, String extraArgs) {
     out.println("Usage:");
     out.printf("    %s [options] %s", programName, extraArgs);
     out.println();
     out.println("Options:");
     for (ProgramOption option : _options.values()) {
       out.print("    ");
       if (!option.requiresArgument()) {
         out.printf("-%-20s", option.value());
       } else {
         out.printf("-%-20s", option.value() + ' ' + option.argumentId());
       }
       out.printf(" : ");
       out.print(option.description());
       if (option.requiresArgument()) {
         out.printf(" [default: %s]", option.defaultValue());
       }
       out.println();
     }
  }
  
  
  @SuppressWarnings("javadoc")
  public static void main(String[] args) {
    new CommandLineOptions(CommandLineOptions.class).printUsage("x", System.out, "file1 ... filen");
  }
  
}
