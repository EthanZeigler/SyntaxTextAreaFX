/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.s13;

import in.co.s13.meta.Generator;
import in.co.s13.meta.Syntax;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.text.Font;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.PlainTextChange;
import org.fxmisc.richtext.StyleSpans;
import org.fxmisc.richtext.StyleSpansBuilder;
import org.reactfx.EventStream;
import org.reactfx.util.Try;

/**
 *
 * @author Nika
 */
public class SyntaxTextAreaFX {

//    private static final String[] KEYWORDS = new String[]{
//        "abstract", "assert", "boolean", "break", "byte",
//        "case", "catch", "char", "class", "const",
//        "continue", "default", "do", "double", "else",
//        "enum", "extends", "final", "finally", "float",
//        "for", "goto", "if", "implements", "import",
//        "instanceof", "int", "interface", "long", "native",
//        "new", "package", "private", "protected", "public",
//        "return", "short", "static", "strictfp", "super",
//        "switch", "synchronized", "this", "throw", "throws",
//        "transient", "try", "void", "volatile", "while"
//    };
//
//    private static String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
//    private static String PAREN_PATTERN = "\\(|\\)";
//    private static String BRACE_PATTERN = "\\{|\\}";
//    private static String BRACKET_PATTERN = "\\[|\\]";
//    private static String SEMICOLON_PATTERN = "\\;";
//    private static String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
//    private static String COMMENT_PATTERN = "//[^\n]*" + "|" + "/\\*(.|\\R)*?\\*/";
//    
//    private static Pattern PATTERN = Pattern.compile(
//            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
//            + "|(?<PAREN>" + PAREN_PATTERN + ")"
//            + "|(?<BRACE>" + BRACE_PATTERN + ")"
//            + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
//            + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
//            + "|(?<STRING>" + STRING_PATTERN + ")"
//            + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
//    );
    private static String Theme = "default";
    private Pattern PATTERN;
    private CodeArea codeArea;
    private ExecutorService executor;
    private String filePath = "", externalThemePath = "";
    public Scene scene;
    private Syntax syntax;

    public static enum FILE_TYPES {
        as, adb, ads, forth, asp, automake, awk,
        bennugd, bibtex, bluespec, boo, c, cg, changelog,
        cmake, cobol, cpp, cpphdr, csharp, css, cuda, d,
        def, desktop, diff, docbook, dosbatch, dot, dpatch,
        dtd, eiffel, erlang, fcl, fortran, fsharp, gap, gdblog,
        genie, glsl, gtkdoc, gtkrc, haddock, haskell, haskellliterate,
        html, idlexelis, imagej, ini, j, jade, java, javascript, json,
        julia, latex, lex, libtool, llvm, m4, makefile, mallard, markdown,
        matlab, mediawiki, modelica, mxml, nemerle, nemo_action, netrexx,
        nsis, objj, ocaml, ocl, octave, ooc, opal, pascal, perl, php, pig,
        pkgconfig, po, protobuf, puppet, python, python3, r, rpmspec, ruby,
        rust, scala, scheme, scilab, sh, sparql, sql, sweave, systemverilog,
        t2t, tcl, thrift, vala, vbnet, verilog, vhdl, xml, yacc, yaml
    };

    public static enum LANGS {
        actionscript, ada, ansforth94, asp,
        automake, awk, bennugd, bibtex,
        bluespec, boo, c, cg, changelog,
        chdr, cmake, cobol, cpp,
        cpphdr, csharp, css, csv,
        cuda, d, def, desktop,
        diff, docbook, dosbatch, dot,
        dpatch, dtd, eiffel, erlang,
        fcl, forth, fortran, fsharp,
        gap, gdb_log, genie, glsl,
        go, gtk_doc, gtkrc, haddock,
        haskell, haskell_literate, html, idl, idl_exelis,
        imagej, ini, j, jade, java,
        javascript, json, julia,
        latex, lex, libtool, llvm,
        lua, m4, makefile, mallard, markdown,
        matlab, mediawiki, meson, modelica,
        mxml, nemerle, nemo_action, netrexx,
        nsis, objc, objj, ocaml,
        ocl, octave, ooc, opal,
        opencl, pascal, perl, php,
        pig, pkgconfig, po, prolog,
        protobuf, puppet, python, python3,
        R, rpmspec, rst, ruby,
        rust, scala, scheme, scilab,
        sh, sml, sparql, sql,
        sweave, systemverilog, t2t, tcl,
        texinfo, thrift, vala, vbnet,
        verilog, vhdl, xml, xslt,
        yacc, yaml
    };

    public static String[] ALT_FILE_TYPES = {"4th"};

    private static LANGS CodingStyle = LANGS.java;

    /**
     * *
     * Default Constructor
     */
    public SyntaxTextAreaFX() {
        this("");
    }

    /**
     *
     * @return Theme Name as String
     */
    public static String getTheme() {
        return Theme;
    }

    /**
     * *
     *
     * @param Theme Name of theme as String, where Theme is the name of the
     * directory available in res/css/Theme/
     *
     */
    public static void setTheme(String Theme) {
        SyntaxTextAreaFX.Theme = Theme;
    }

    /**
     * *
     * Sets theme to default
     */
    public static void setThemeDefault() {
        SyntaxTextAreaFX.Theme = "default";
    }

    /**
     * *
     *
     * @param fileExtension a string file extension
     * @return boolean value true/false if fileExtension is supported
     */
    private static boolean contains(String fileExtension) {

        for (FILE_TYPES c : FILE_TYPES.values()) {
            if (c.name().equals(fileExtension)) {
                return true;
            }
        }

        return false;
    }

    /**
     * *
     *
     * @param file String Path to file Reads file from given path and set
     * contents to SyntaxTextAreaFX
     */
    public SyntaxTextAreaFX(String file) {
        filePath = file;
        executor = Executors.newSingleThreadExecutor();
        codeArea = new CodeArea();

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
//        EventStream<PlainTextChange> textChanges = codeArea.plainTextChanges();
//        textChanges
//                .successionEnds(Duration.ofMillis(500))
//                .supplyTask(this::computeHighlightingAsync)
//                .awaitLatest(textChanges)
//                .map(Try::get)
//                .subscribe(this::applyHighlighting);
        String fileExtension = "";
        if (file.trim().length() > 1 && file.contains(".")) {
            fileExtension = file.substring(file.lastIndexOf(".") + 1).trim().toLowerCase();
        }
        if (file.trim().length() > 1 && file.contains(".") && SyntaxTextAreaFX.contains(fileExtension)) {
            // setCodingStyle();
            setCodingStyle(getCodingStyleFromFileType(FILE_TYPES.valueOf(fileExtension)));

        } else {
            generatePattern();
            codeArea.getStylesheets().add(SyntaxTextAreaFX.class.getResource("res/css/default/java.css").toExternalForm());

        }
        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .successionEnds(Duration.ofMillis(500))
                .supplyTask(this::computeHighlightingAsync)
                .awaitLatest(codeArea.richChanges())
                .filterMap(t -> {
                    if (t.isSuccess()) {
                        return Optional.of(t.get());
                    } else {
                        t.getFailure().printStackTrace();
                        return Optional.empty();
                    }
                })
                .subscribe(this::applyHighlighting);

        this.setText(this.readFile(filePath));

    }

    /**
     * *
     *
     * @param text sets text to SyntaxTextAreaFX
     */
    public void setText(String text) {
        codeArea.replaceText(0, 0, text);
        codeArea.getUndoManager().forgetHistory();
        codeArea.getUndoManager().mark();

    }

    /**
     *
     * @return Text as String from SyntaxTextAreaFX
     */
    public String getText() {
        return codeArea.getText();

    }

    /**
     * *
     *
     * @param text appends text to SyntaxTextAreaFX
     */
    public void appendText(String text) {
        codeArea.appendText(text);

    }

    /**
     * **
     *
     * @param v sets Font of SyntaxTextAreaFX
     */
    public void setFont(Font v) {
        codeArea.setFont(v);
    }

    /**
     * **
     *
     * @return the Graphical Node, which can be added to Layout
     */
    public CodeArea getNode() {
        return codeArea;
    }

    /**
     * *
     *
     * @return Path to External Theme
     */
    public String getExternalThemePath() {
        return externalThemePath;
    }

    /**
     * *
     *
     * @param externalThemePath sets external theme path
     */
    public void setExternalThemePath(String externalThemePath) {
        this.externalThemePath = externalThemePath;
    }

    /**
     * *
     * saves the contents of SyntaxTextAreaFX to @FilePath
     */
    public void save() {
        if (filePath.trim().length() > 0) {
            try {
                write(new File(filePath), getText());
            } catch (IOException ex) {
                Logger.getLogger(SyntaxTextAreaFX.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.err.println("File Path is Empty !!!");
        }
    }

    /**
     * *
     *
     * @param filePath saves content to filePath
     */
    public void saveAs(String filePath) {
        this.filePath = filePath;
        try {
            write(new File(filePath), getText());
        } catch (IOException ex) {
            Logger.getLogger(SyntaxTextAreaFX.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * *
     *
     * @return get the Coding Style of SyntaxTextAreaFX
     */
    public LANGS getCodingStyle() {
        return CodingStyle;
    }

    /**
     * *
     *
     * @param CodingStyle sets the coding style of the SyntaxTextAreaFX
     */
    public void setCodingStyle(LANGS CodingStyle) {
        SyntaxTextAreaFX.CodingStyle = CodingStyle;

        if (externalThemePath.trim().length() == 0) {
            codeArea.getStylesheets().add(SyntaxTextAreaFX.class.getResource("res/css/" + getTheme() + "/" + getCodingStyle() + ".css").toExternalForm());
        } else {
            codeArea.getStylesheets().add(readFile(externalThemePath + "/" + getCodingStyle() + ".css"));
        }
        generatePattern();
    }

    /**
     * *
     *
     * @param CSScode add CSS code to SyntaxTextAreaFX
     */
    public void addStyleSheet(String CSScode) {
        codeArea.getStylesheets().add(CSScode);
    }

    /**
     * **
     * Remove all CSS coding sheets
     */
    public void clearStyleSheets() {
        codeArea.getStylesheets().clear();
    }

    /**
     * *
     *
     * @return all CSS file as a List of Strings
     */
    public ObservableList<String> getAllStyleSheets() {
        return codeArea.getStylesheets();
    }

    /**
     * *
     *
     * @param file File to be saved
     * @param text Content to write
     * @throws IOException if unable to find the file path
     */
    private void write(File file, String text) throws IOException {
        file.getParentFile().mkdirs();
        try (FileWriter fw = new FileWriter(file);
                PrintWriter pw = new PrintWriter(fw)) {
            pw.print(text);
            pw.close();
            fw.close();
        }

    }

    /**
     * *
     *
     * @param path path to file
     * @return contents of the file
     */
    private String readFile(String path) {
        String str = "";
        if (path.trim().length() > 1) {
            try {
                Charset encoding = Charset.defaultCharset();
                byte[] encoded = Files.readAllBytes(Paths.get(path));
                str = new String(encoded, encoding);
            } catch (IOException ex) {
                Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return str;
    }

    /**
     * *
     *
     * @return which computes the Highlighting
     */
    private Task<StyleSpans<Collection<String>>> computeHighlightingAsync() {
        String text = codeArea.getText();
        Task<StyleSpans<Collection<String>>> task = new Task<StyleSpans<Collection<String>>>() {
            @Override
            protected StyleSpans<Collection<String>> call() throws Exception {
                return computeHighlighting(text);
            }
        };
        executor.execute(task);
        return task;
    }

    /**
     * *
     *
     * @param highlighting Apply Highlighting style to SyntaxTextAreaFX
     */
    private void applyHighlighting(StyleSpans<Collection<String>> highlighting) {
        codeArea.setStyleSpans(0, highlighting);
    }

    /**
     * **
     *
     * @param text computes Highlighting on provided text
     * @return Collection
     */
    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder
                = new StyleSpansBuilder<>();
        //generatePattern();
        while (matcher.find()) {
            String styleClass
                    = getStyleClass(matcher);
            /* never happens */ assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

    private LANGS getCodingStyleFromFileType(FILE_TYPES filetype) {
        LANGS language = LANGS.java;
        switch (filetype) {
            case as:
                language = LANGS.actionscript;
                break;
            case adb:
            case ads:
                language = LANGS.ada;
                break;
            case asp:
                break;
            case automake:
                break;
            case awk:
                break;
            case bennugd:
                break;
            case bibtex:
                break;
            case bluespec:
                break;
            case boo:
                break;
            case c:
                break;
            case cg:
                break;
            case changelog:
                break;
            case cmake:
                break;
            case cobol:
                break;
            case cpp:
                break;
            case cpphdr:
                break;
            case csharp:
                break;
            case css:
                break;
            case cuda:
                break;
            case d:
                break;
            case def:
                break;
            case desktop:
                break;
            case diff:
                break;
            case docbook:
                break;
            case dosbatch:
                break;
            case dot:
                break;
            case dpatch:
                break;
            case dtd:
                break;
            case eiffel:
                break;
            case erlang:
                break;
            case fcl:
                break;
            case fortran:
                break;
            case fsharp:
                break;
            case gap:
                break;
            case gdblog:
                break;
            case genie:
                break;
            case glsl:
                break;
            case gtkdoc:
                break;
            case gtkrc:
                break;
            case haddock:
                break;
            case haskell:
                break;
            case haskellliterate:
                break;
            case html:
                break;
            case idlexelis:
                break;
            case imagej:
                break;
            case ini:
                break;
            case j:
                break;
            case jade:
                break;
            case javascript:
                break;
            case json:
                break;
            case julia:
                break;
            case latex:
                break;
            case lex:
                break;
            case libtool:
                break;
            case llvm:
                break;
            case m4:
                break;
            case makefile:
                break;
            case mallard:
                break;
            case markdown:
                break;
            case matlab:
                break;
            case mediawiki:
                break;
            case modelica:
                break;
            case mxml:
                break;
            case nemerle:
                break;
            case nemo_action:
                break;
            case netrexx:
                break;
            case nsis:
                break;
            case objj:
                break;
            case ocaml:
                break;
            case ocl:
                break;
            case octave:
                break;
            case ooc:
                break;
            case opal:
                break;
            case pascal:
                break;
            case perl:
                break;
            case php:
                break;
            case pig:
                break;
            case pkgconfig:
                break;
            case po:
                break;
            case protobuf:
                break;
            case puppet:
                break;
            case python:
                break;
            case python3:
                break;
            case r:
                break;
            case rpmspec:
                break;
            case ruby:
                break;
            case rust:
                break;
            case scala:
                break;
            case scheme:
                break;
            case scilab:
                break;
            case sh:
                break;
            case sparql:
                break;
            case sql:
                break;
            case sweave:
                break;
            case systemverilog:
                break;
            case t2t:
                break;
            case tcl:
                break;
            case thrift:
                break;
            case vala:
                break;
            case vbnet:
                break;
            case verilog:
                break;
            case vhdl:
                break;
            case xml:
                break;
            case yacc:
                break;
            case yaml:
                break;
            default:
                break;
        }
        return language;
    }

    private void loadLanguage() {
        switch (getCodingStyle()) {
            case actionscript:
                break;

            case ada:
                break;
            case ansforth94:
                break;
            case asp:
                break;
            case automake:
                break;
            case awk:
                break;
            case bennugd:
                break;
            case bibtex:
                break;
            case bluespec:
                break;
            case boo:
                break;
            case c:
                break;
            case cg:
                break;
            case changelog:
                break;
            case cmake:
                break;
            case cobol:
                break;
            case cpp:
                break;
            case cpphdr:
                break;
            case csharp:
                break;
            case css:
                break;
            case cuda:
                break;
            case d:
                break;
            case def:
                break;
            case desktop:
                break;
            case diff:
                break;
            case docbook:
                break;
            case dosbatch:
                break;
            case dot:
                break;
            case dpatch:
                break;
            case dtd:
                break;
            case eiffel:
                break;
            case erlang:
                break;
            case fcl:
                break;
            case forth:
                break;
            case fortran:
                break;
            case fsharp:
                break;
            case gap:
                break;
            case gdb_log:
                break;
            case genie:
                break;
            case glsl:
                break;
            case gtk_doc:
                break;
            case gtkrc:
                break;
            case haddock:
                break;
            case haskell:
                break;
            case haskell_literate:
                break;
            case html:
                break;
            case idl_exelis:
                break;
            case imagej:
                break;
            case ini:
                break;
            case j:
                break;
            case jade:
                break;
            case javascript:
                break;
            case json:
                break;
            case julia:
                break;
            case latex:
                break;
            case lex:
                break;
            case libtool:
                break;
            case llvm:
                break;
            case m4:
                break;
            case makefile:
                break;
            case mallard:
                break;
            case markdown:
                break;
            case matlab:
                break;
            case mediawiki:
                break;
            case modelica:
                break;
            case mxml:
                break;
            case nemerle:
                break;
            case nemo_action:
                break;
            case netrexx:
                break;
            case nsis:
                break;
            case objj:
                break;
            case ocaml:
                break;
            case ocl:
                break;
            case octave:
                break;
            case ooc:
                break;
            case opal:
                break;
            case pascal:
                break;
            case perl:
                break;
            case php:
                break;
            case pig:
                break;
            case pkgconfig:
                break;
            case po:
                break;
            case protobuf:
                break;
            case puppet:
                break;
            case python:
                break;
            case python3:
                break;
            case R:
                break;
            case rpmspec:
                break;
            case ruby:
                break;
            case rust:
                break;
            case scala:
                break;
            case scheme:
                break;
            case scilab:
                break;
            case sh:
                break;
            case sparql:
                break;
            case sql:
                break;
            case sweave:
                break;
            case systemverilog:
                break;
            case t2t:
                break;
            case tcl:
                break;
            case thrift:
                break;
            case vala:
                break;
            case vbnet:
                break;
            case verilog:
                break;
            case vhdl:
                break;
            case xml:
                break;
            case yacc:
                break;
            case yaml:
                break;
            default:
                break;

        }
    }

    /**
     * *
     * @param
     *
     * @return s
     */
    private String getStyleClass(Matcher matcher) {
        String styleClass = "";

        return styleClass;
    }

    void generatePattern() {

        switch (getCodingStyle()) {
            case actionscript:
                break;

            case ada:

                break;
            case forth:

                break;
            case asp:
                break;
            case automake:
                break;
            case awk:
                break;
            case bennugd:
                break;
            case bibtex:
                break;
            case bluespec:
                break;
            case boo:
                break;
            case c:
                break;
            case cg:
                break;
            case changelog:
                break;
            case cmake:
                break;
            case cobol:
                break;
            case cpp:
                break;
            case cpphdr:
                break;
            case csharp:
                break;
            case css:
                break;
            case cuda:
                break;
            case d:
                break;
            case def:
                break;
            case desktop:
                break;
            case diff:
                break;
            case docbook:
                break;
            case dosbatch:
                break;
            case dot:
                break;
            case dpatch:
                break;
            case dtd:
                break;
            case eiffel:
                break;
            case erlang:
                break;
            case fcl:
                break;
            case fortran:
                break;
            case fsharp:
                break;
            case gap:
                break;
            case gdb_log:
                break;
            case genie:
                break;
            case glsl:
                break;
            case gtk_doc:
                break;
            case gtkrc:
                break;
            case haddock:
                break;
            case haskell:
                break;
            case haskell_literate:
                break;
            case html:
                break;
            case idl_exelis:
                break;
            case imagej:
                break;
            case ini:
                break;
            case j:
                break;
            case jade:
                break;

            case javascript:
                break;
            case json:
                break;
            case julia:
                break;
            case latex:
                break;
            case lex:
                break;
            case libtool:
                break;
            case llvm:
                break;
            case m4:
                break;
            case makefile:
                break;
            case mallard:
                break;
            case markdown:
                break;
            case matlab:
                break;
            case mediawiki:
                break;
            case modelica:
                break;
            case mxml:
                break;
            case nemerle:
                break;
            case nemo_action:
                break;
            case netrexx:
                break;
            case nsis:
                break;
            case objj:
                break;
            case ocaml:
                break;
            case ocl:
                break;
            case octave:
                break;
            case ooc:
                break;
            case opal:
                break;
            case pascal:
                break;
            case perl:
                break;
            case php:
                break;
            case pig:
                break;
            case pkgconfig:
                break;
            case po:
                break;
            case protobuf:
                break;
            case puppet:
                break;
            case python:
                break;
            case python3:
                break;
            case R:
                break;
            case rpmspec:
                break;
            case ruby:
                break;
            case rust:
                break;
            case scala:
                break;
            case scheme:
                break;
            case scilab:
                break;
            case sh:
                break;
            case sparql:
                break;
            case sql:
                break;
            case sweave:
                break;
            case systemverilog:
                break;
            case t2t:
                break;
            case tcl:
                break;
            case thrift:
                break;
            case vala:
                break;
            case vbnet:
                break;
            case verilog:
                break;
            case vhdl:
                break;
            case xml:
                break;
            case yacc:
                break;
            case yaml:
                break;
            default: //JAVA case is default

                break;
        }

    }

}
