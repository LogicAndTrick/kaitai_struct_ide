# Kaitai Struct IDE

**Highly experimental!**

A proof-of-concept IDE-style interface for
[Kaitai Struct](https://github.com/kaitai-io/kaitai_struct).

## How to use

1. Run the project
2. Click "Browse" on the right side an open a binary file.
   You will see a hex representation of the file open in the right panel.
3. Do the same on the left panel with your KSY file or simply start
   typing your KSY spec in the left panel.
4. Clicking amongst the hex values in the right panel will populate
   the values in the table at the bottom. This is useful for exploring
   potential data types in a format you're not familiar with.

![Screenshot](https://i.imgur.com/ZeVJgIj.png)

An example of the hex dump from the previous screenshot:

    0000:  10 00 00 00                                     | len1: 16
                       42 00 00 00                         | block1.number1: 66
                                   43 00 00 00             | block1.number2: 67
                                               FF FF FF FF | [             .... ]
    0001:  FF FF FF FF                                     | [ ....             ]
                       08 00 00 00                         | len2: 8
                                   44 00 00 00             | block2.number1: 68
                                               45 00 00 00 | block2.number2: 69
    0002:  EE 00 00 00                                     | finisher: 238

The first four numbers are the line number in hexadecimal. Each line is 16
bytes in length. A line that contains multiple variables will be printed
across multiple lines so each value can be printed on the right.

The values printed on the right are in respect to the root object. The first
value, `len1`, is owned by the root object. The second value, `block1.number1` is
the value of `number1` in the root's `block1` object.

Values in `[` brackets `]` were skipped and are not part of any evaluated value.

## Code structure

- `io.kaitai.struct`
    - `runtime`: A dynamic Kaitai interpreting runtime written in Java.
        - `conditionals`: if tests and repeat specs
        - `expressions`: Dynamic expression evaluator (note: some expression types are still not implemented)
        - `results`: values that store results
        - `types`: native and user-defined Kaitai data types
    - `visualizer`: The UI part of the project
        - `ui`: UI controls and hex printing utilities
        - `Editor`: The main UI dialog of the IDE
    - `KaitaiStream.java`: Slightly modified version of the Java runtime's stream

## Disclaimers

1. I'm a terrible Java developer. Forgive me. I don't know how to use
   package managers either...
2. Some of this would be better off in Scala. I couldn't work out how
   to get sbt working so I gave up and used Java instead.
3. **Many important IDE functions are still missing!** You can open files
   and you can type KSY specs, but that's it. You can't even save anything
   yet. It's usable, but to save your work, copy the results into some other
   editor and save it there. This thing may crash sometimes so do that often
   or you might lose your work.
4. I made this project because I thought writing a dynamic runtime would be
   fun. It was! I didn't really think any further ahead than that, but if
   this can be useful to anybody, it'd be really cool.
5. The interface is designed for me, I don't know if it's good for others
   or not. It's partly inspired by the interface of 010 editor, where you
   can edit a binary template in one panel and poke around the binary in the
   other side. 
   The general idea is that the data is printed right next to the
   binary, not in a separate abstract tree or anything. I think that having
   the values printed next to the hex dump is a very natural way to work when
   reverse engineering an unknown binary structure.
   It might be a little odd but I think it works really well. Maybe if this
   project evolves, it could also have the more traditional hex dump and the
   abstract tree of values as a different view option.
