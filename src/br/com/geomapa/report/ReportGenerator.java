/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.geomapa.report;

import br.com.geomapa.util.ReflectionUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author paulocanedo
 */
public class ReportGenerator {

    private final String charset = "UTF-8";
    private File template;
    private InputStream stream;

    public ReportGenerator(File template) {
        this.template = template;
    }

    public ReportGenerator(InputStream templateStream) throws IOException {
        this.stream = templateStream;
        this.template = File.createTempFile("geomapa_", ".template");
        this.template.deleteOnExit();

        FileOutputStream out = new FileOutputStream(template);

        byte buffer[] = new byte[1024 * 8];
        int readed = 0;
        while ((readed = templateStream.read(buffer)) >= 0) {
            out.write(buffer, 0, readed);
        }

        out.close();
    }

    public void generate(File output, Map<String, Object> values) throws FileNotFoundException, IOException {
        generate(output, values, null);
    }

    public void generate(File output, Map<String, Object> values, List<Map<String, Object>> repeatedValuesOds) throws FileNotFoundException, IOException {
        ZipFile zipfile = new ZipFile(template);

        Enumeration<? extends ZipEntry> entries = zipfile.entries();
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(output));
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            InputStream in = zipfile.getInputStream(entry);

            out.putNextEntry(new ZipEntry(entry.getName()));
            if (entry.getName().equals("content.xml")) {
                StringBuilder templateText = prepare(in);

                if (repeatedValuesOds != null) {
                    insertRepeatedValues(templateText, repeatedValuesOds);
                }

                StringBuilder toWrite = merge(templateText.toString(), values);
                out.write(fixOdtTableProblem(toWrite).getBytes(charset));
            } else {
                byte[] buffer = new byte[1024];
                int readed = 0;
                while ((readed = in.read(buffer)) > 0) {
                    out.write(buffer, 0, readed);
                }
            }

            out.closeEntry();
        }
        out.close();
    }

    private StringBuilder prepare(InputStream templateXmlStream) throws FileNotFoundException, IOException {
        BufferedReader templateReader = new BufferedReader(new InputStreamReader(templateXmlStream, charset));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = templateReader.readLine()) != null) {
            sb.append(line);
        }

        return sb;
    }

    private static StringBuilder merge(String templateText, Map<String, Object> values) {
        int indexControlList = -1;
        String nameListControl = null;
        StringBuilder outbuilder = new StringBuilder();
        StringTokenizer st = new StringTokenizer(templateText, " ,;\'\"()\t<>&", true);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (token.startsWith("$")) {
                int indexOf = indexOf(token);
                if (indexOf > 0) {
                    token = token.substring(0, indexOf);
                }
                indexOf = token.indexOf('.');
                String varname = indexOf > 0 ? token.substring(1, indexOf) : token.substring(1);
                String fieldname = indexOf > 0 ? token.substring(indexOf + 1) : null;

                Object object = values.get(varname);
                if (object instanceof List) {
                    if (nameListControl == null) {
                        nameListControl = varname;
                    }

                    if (nameListControl.equals(varname)) {
                        indexControlList++;
                    }
                }
                String value = getValue(fieldname, varname, object, indexControlList);
                outbuilder.append(value);
            } else {
                outbuilder.append(token);
            }
        }
        return outbuilder;
    }

    private static String getValue(String fieldname, String varname, Object value, int indexControlList) {
        if (value == null) {
            return "";
        }

        if (value instanceof List) {
            List list = (List) value;
            value = list.get(indexControlList);
        }

        if (fieldname == null) {
            return value == null ? "" : value.toString();
        } else {
            Class c = value.getClass();
            Method method = ReflectionUtils.findMethod(c, "get", fieldname);
            if (method == null) {
                method = ReflectionUtils.findMethod(c, fieldname);
                if (method == null) {
                    throw new IllegalArgumentException(String.format("O campo %s%s não existe.", varname == null ? "" : varname + ".", fieldname));
                }
            }
            Object result = ReflectionUtils.invokeMethod(value, method);

            return result == null ? "" : result.toString();
        }
    }

    private static int indexOf(String text) {
        char[] specialChars = new char[]{'<', '>', '|', '\\', '{', '}', '(', ')', ',', ';', '\'', '\"', '&'};
        int index = Integer.MAX_VALUE;
        for (char c : specialChars) {
            int indexOf = text.indexOf(c);
            if (indexOf >= 0 && indexOf < index) {
                index = indexOf;
            }
        }

        if (text.indexOf('.') != text.lastIndexOf('.')) {
            index = Math.min(index, text.lastIndexOf('.'));
        }
        return index == Integer.MAX_VALUE ? -1 : index;
    }

    private static String fixOdtTableProblem(StringBuilder sb) {
        int charactersDeleted = 0, lastStartTag = 0, lastEndTag = 0;
        TagReader tagReader = new TagReader(sb.toString());
        String tag;
        String lastTag = "";
        while ((tag = tagReader.nextTag()) != null) {
            if (lastTag.startsWith("<text") && !lastTag.endsWith("/>") && tag.startsWith("<table")) {
                sb.delete(lastStartTag - charactersDeleted, lastEndTag - charactersDeleted + 1);
                charactersDeleted += lastEndTag - lastStartTag + 1;
            }

            if (tag.equals("</text:p>") && lastTag.equals("</table:table>")) {
                int startTag = tagReader.getStartTag();
                int endTag = tagReader.getEndTag();

                sb.delete(startTag - charactersDeleted, endTag - charactersDeleted + 1);
            }

            lastStartTag = tagReader.getStartTag();
            lastEndTag = tagReader.getEndTag();
            lastTag = tag;
        }

        return sb.toString();
    }

    public static StringBuilder insertRepeatedValues(StringBuilder sb, List<Map<String, Object>> list) {
        boolean flag = false;
        int tableRowStartTag = 0, tableRowEndTag = 0, lastEndTag = 0;
        TagReader tagReader = new TagReader(sb.toString());

        String currentTag;
        while ((currentTag = tagReader.nextTag()) != null) {
            int startTag = tagReader.getStartTag();
            if ((startTag - lastEndTag) > 0) {
                String tagValue = sb.substring(lastEndTag + 1, startTag).trim();
                if (tagValue.startsWith("{")) {
                    flag = true;
                }
            }


            if (currentTag.startsWith("<table:table-row")) {
                tableRowStartTag = tagReader.getStartTag();
            }

            if (currentTag.startsWith("</table:table-row") && flag == true) {
                tableRowEndTag = tagReader.getEndTag();
                flag = false;
                String rowTemplate = sb.substring(tableRowStartTag, tableRowEndTag + 1);
                sb.delete(tableRowStartTag, tableRowEndTag + 1);
                
                Matcher matcher = pattern.matcher(rowTemplate);
                for (Map<String, Object> hashMap : list) {
                    String rowFilled = rowTemplate.intern();
                    matcher.reset();
                    
                    while (matcher.find()) {
                        String group = matcher.group();
                        String var = group.substring(1, group.length()-1);
                        Object value = hashMap.get(var);

                        rowFilled = rowFilled.replaceFirst(spattern, value == null ? "" : value.toString());
                    }

                    sb.insert(tableRowStartTag, rowFilled);
                }
            }

            lastEndTag = tagReader.getEndTag();
        }
        return sb;
    }
    
    public static void main(String... args) {
        try {
            StringBuilder prepare = new ReportGenerator(new File("")).prepare(new FileInputStream(new File("/Users/paulocanedo/Desktop/content.xml")));
            TagReader tagReader = new TagReader(prepare.toString());
            
            String current;
            while((current = tagReader.nextTag()) != null) {
                System.out.println(current);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ReportGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReportGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private final static String spattern = "\\{(\\w+)\\}";
    private final static Pattern pattern = Pattern.compile(spattern);
}

class TagReader {

    private String text;
    private int currentIndex = 0;
    private int currentStartTag = -1;
    private int currentCloseTag = -1;
    private int startTag = -1;
    private int endTag = -1;

    public TagReader(String text) {
        this.text = text;
    }

    public String nextTag() {
        for (; currentIndex < text.length(); currentIndex++) {
            if (text.charAt(currentIndex) == '<') {
                currentStartTag = currentIndex;
            } else if (text.charAt(currentIndex) == '>') {
                currentCloseTag = currentIndex;
            }

            if (currentStartTag >= 0 && currentCloseTag > 0) {
                String toReturn = text.substring(currentStartTag, currentCloseTag + 1);
                startTag = currentStartTag;
                endTag = currentCloseTag;

                currentStartTag = currentCloseTag = -1;

                currentIndex++;
                return toReturn;
            }
        }
        return null;
    }

    public int getStartTag() {
        return startTag;
    }

    public int getEndTag() {
        return endTag;
    }
}