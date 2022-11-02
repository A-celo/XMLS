import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

    public class Sales extends DefaultHandler {
        private static final String CLASS_NAME = Sales.class.getName();
        private final static Logger LOG = Logger.getLogger(CLASS_NAME);
        private SAXParser parser = null;
        private SAXParserFactory spf;
        private double totalS;
        private boolean inSales;
        private String currentElement;
        private String id;
        private String name;
        private String lastName;
        private String sales;
        private String state;
        private String dept;

        private String keyword;

        private HashMap<String, Double> stateSales;
        private HashMap<String, Double> deptSales;
        public Sales() {
            super();
            spf = SAXParserFactory.newInstance();
            // verificar espacios de nombre
            spf.setNamespaceAware(true);
            // validar que el documento esté bien formado (well formed)
            spf.setValidating(true);

            stateSales = new HashMap<>();
            deptSales = new HashMap<>();
        }

        private void process(File file) {
            try {
                parser = spf.newSAXParser();
            } catch (SAXException | ParserConfigurationException e) {
                LOG.severe(e.getMessage());
                System.exit(1);
            }
            System.out.println("\nStarting parsing of " + file + "\n");
            try {
                keyword = state;
                parser.parse(file, this);
            } catch (IOException | SAXException e) {
                LOG.severe(e.getMessage());
            }
        }

        @Override
        public void startDoc() throws SAXException {
            totalS = 0.0;
        }

        @Override
        public void endDoc() throws SAXException {
            Set<Map.Entry<String, Double>> State = stateSales.entrySet();
            Set<Map.Entry<String, Double>> Dept = deptSales.entrySet();

            System.out.println("Tabla de información de ventas");
            System.out.println("-------------------------------------");
            System.out.println("Estados");
            for (Map.Entry<String, Double> entry : State) {
                System.out.printf("%-10.10s $%,7.2f\n", entry.getKey(), entry.getValue());
            }
            System.out.println();
            System.out.println("Departamentos");
            for(Map.Entry<String, Double> entry : Dept){
                System.out.printf("%-10.10s $%,7.2f\n", entry.getKey(), entry.getValue());
            }
            System.out.println("-------------------------------------");
            System.out.println();
            System.out.printf("Ventas en total: $%,7.2f\n", totalS);
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            if (localName.equals("sale_record")) {
                inSales = true;
            }
            currentElement = localName;
        }
        @Override
        public void characters(char[] bytes, int start, int length) throws SAXException {

            switch (currentElement) {
                case "id":
                    this.id = new String(bytes, start, length);
                    break;
                case "first_name":
                    this.name = new String(bytes, start, length);
                    break;
                case "last_name":
                    this.lastName = new String(bytes, start, length);
                    break;
                case "sales":
                    this.sales = new String(bytes, start, length);
                    break;
                case "state":
                    this.state = new String(bytes, start, length);
                    break;
                case "department":
                    this.dept = new String(bytes, start, length);
                    break;
            }
        }
        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if (localName.equals("sale_record")) {
                double c = 0.0;
                try {
                    c = Double.parseDouble(this.sales);
                } catch (NumberFormatException e) {
                    LOG.severe(e.getMessage());
                }
                if (stateSales.containsKey(this.state)) {
                    double sum = stateSales.get(this.state);
                    stateSales.put(this.state, sum + c);
                } else {
                    stateSales.put(this.state, c);
                }
                if (deptSales.containsKey(this.dept)) {
                    double sum = deptSales.get(this.dept);
                    deptSales.put(this.dept, sum + c);
                } else {
                    deptSales.put(this.dept, c);
                }
                totalS = totalS + c;
                inSales = false;
            }
        }
        private void printRecord() {
            System.out.printf("%4.4s %-10.10s %-10.10s %9.9s %-10.10s %-15.15s\n",
                    id, name, lastName, sales, state, dept);
        }
        public void main(String args[]) {
            if (args.length == 0) {
                LOG.severe("No file to process. Usage is:" + "\njava DeptSalesReport <keyword>");
                return;
            }
            File xmlFile = new File(args[0]);
            Sales handler = new Sales();
            handler.process(xmlFile);
        }
    }