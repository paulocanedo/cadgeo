/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DCGenerator.java
 *
 * Created on 26/08/2010, 14:38:56
 */
package br.com.geografico.ztest;

import br.com.geomapa.util.SecurityUtils;
import br.com.geomapa.util.MiscUtils;
import br.com.geomapa.export.DCListModel;
import br.com.geomapa.export.DCTableModel;
import br.com.geomapa.export.DadosCartograficosOds;
import br.com.geomapa.geodesic.GeodesicEnum;
import br.com.geomapa.geodesic.Hemisphere;
import br.com.geomapa.graphic.cad.geo.GeodesicPoint;
import br.com.geomapa.geodesic.InvalidGeodesicPointException;
import br.com.geomapa.geodesic.datum.Datum;
import br.com.geomapa.geodesic.datum.GRS80Datum;
import br.com.geomapa.importer.PointImporterHandle;
import br.com.geomapa.importer.AstechRTFImporter;
import br.com.geomapa.importer.AutotopoImporter;
import br.com.geomapa.importer.CSVPointImporter;
import br.com.geomapa.importer.CalculoAreaRTFImporter;
import br.com.geomapa.importer.PointImporter;
import br.com.geomapa.importer.PointImporterListHandler;
import br.com.geomapa.importer.RinexImporter;
import br.com.geomapa.importer.TopconDOCXImporter;
import br.com.geomapa.importer.TrimblePointImporter;
import br.com.geomapa.ui.FileFinder;
import java.awt.Desktop;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.text.BadLocationException;

/**
 *
 * @author paulocanedo
 */
public class DCGenerator extends javax.swing.JFrame {

    private DadosCartograficosOds dcIncraOds;
    private DadosCartograficosOds dcTerraLegalOds;
    private DadosCartograficosOds memoTerraLegalOds;
    private DadosCartograficosOds terraLegalValidacaoOds;
    private DCTableModel dataShowModel = new DCTableModel();
    private JTable dataShowTable;
    private DCListModel allDataListModel = new DCListModel();
    private JList allDataList = new JList(allDataListModel);
    private RinexAnalyserDirectory rinexAnalyser;
    private Datum datum = new GRS80Datum();
    private int utmZone = 22; //nao estah sendo usado
    private Hemisphere hemisphere = Hemisphere.SOUTH;
    
    private GeodesicEnum[] txtOrder = new GeodesicEnum[]{
        GeodesicEnum.NAME,
        GeodesicEnum.UNKOWN,
        GeodesicEnum.EAST,
        GeodesicEnum.QUALITY_X,
        GeodesicEnum.NORTH,
        GeodesicEnum.QUALITY_Y,
        GeodesicEnum.ELIPSOIDAL_HEIGHT,
        GeodesicEnum.QUALITY_Z,
        GeodesicEnum.MEASUREMENT_METHOD,
        GeodesicEnum.LIMIT_TYPE,
        GeodesicEnum.BORDERING,};
    private DefaultListModel fileModel = new DefaultListModel();

    /** Creates new form DCGenerator */
    public DCGenerator() {
        initComponents();

        dataShowTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataShowTable.setDragEnabled(true);

        JScrollPane scrollPane = new JScrollPane(dataShowTable);
        jPanel1.add(scrollPane);

        JScrollPane scrollPane2 = new JScrollPane(allDataList);
        jPanel2.add(scrollPane2);

        try {
            dcIncraOds = new DadosCartograficosOds(getClass().getResourceAsStream("/br/com/geografico/resources/templates/dados_cartograficos.ods"));
            dcTerraLegalOds = new DadosCartograficosOds(getClass().getResourceAsStream("/br/com/geografico/resources/templates/dc_terra_legal.ods"));
            memoTerraLegalOds = new DadosCartograficosOds(getClass().getResourceAsStream("/br/com/geografico/resources/templates/memo_terra_legal.odt"));
            terraLegalValidacaoOds = new DadosCartograficosOds(getClass().getResourceAsStream("/br/com/geografico/resources/templates/terra_legal_validacao.ods"));
        } catch (IOException ex) {
            Logger.getLogger(DCGenerator.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Falha ao abrir arquivo de modelo para gerar planilha.\n"
                    + "Não será possível gerar a planilha.");
        }

        jCheckBox1.setSelected(true);
        initListeners();
    }

    private void importDataToTable(File file, DCTableModel model) throws FileNotFoundException, IOException, InvalidGeodesicPointException, BadLocationException {
        PointImporterHandle handle = new CalculoAreaPointImporterHandle(model);

        String filename = file.getName().toLowerCase();
        FileInputStream fis = new FileInputStream(file);
        PointImporter importer = null;
        if (filename.endsWith(".xlsx") || filename.endsWith(".xls")) {
            importer = new AutotopoImporter(handle, fis, PointImporter.defaultCalculoAreaSequence, filename.endsWith(".xls"), utmZone, hemisphere, datum);
        } else if (file.getName().toLowerCase().endsWith(".rtf")) {
            importer = new CalculoAreaRTFImporter(handle, fis, PointImporter.defaultCalculoAreaSequence, utmZone, hemisphere, datum);
        } else {
            throw new IllegalArgumentException(String.format("Formato de arquivo não suportado."));
        }
        importer.importData();
    }

    private void importDataToList(File file, DCListModel model) throws FileNotFoundException, IOException, InvalidGeodesicPointException {
        FileInputStream fis = new FileInputStream(file);
        PointImporterHandle handle = new ListPointImporterHandle(model);

        String filename = file.getName().toLowerCase();
        List<PointImporter> importers = new ArrayList<PointImporter>();
        if (filename.endsWith(".csv")) {
            importers.add(new CSVPointImporter(handle, fis, PointImporter.csvSequence, utmZone, hemisphere, datum));
        } else if (filename.endsWith(".txt")) {
            importers.add(new CSVPointImporter(handle, fis, txtOrder, ";", utmZone, hemisphere, datum));
        } else if (filename.endsWith(".html")) {
            TrimblePointImporter.TrimbleResultsFileFilter fileFilter = new TrimblePointImporter.TrimbleResultsFileFilter(file);
            for (File f : file.getParentFile().listFiles(fileFilter)) {
                importers.add(new TrimblePointImporter(handle, new FileInputStream(f), TrimblePointImporter.trimbleSequence, utmZone, hemisphere, datum));
            }
        } else if (filename.endsWith(".rtf")) {
            try {
                importers.add(new AstechRTFImporter(handle, fis, PointImporter.astechSequence, utmZone, hemisphere, datum));
            } catch (BadLocationException ex) {
                throw new IOException(ex);
            }
        } else if (filename.endsWith(".docx")) {
            importers.add(new TopconDOCXImporter(handle, fis, PointImporter.topconPlusSequence, utmZone, hemisphere, datum));
        } else {
            throw new RuntimeException("Formato não suportado.");
        }

        for (PointImporter importer : importers) {
            importer.importData();
        }

        DefaultListModel modelSourceList = (DefaultListModel) fileSourcesList.getModel();
        if (!modelSourceList.contains(file)) {
            modelSourceList.addElement(file);
        }
    }

    private void copySelectedToTable(JList list, DCTableModel model) {
        Object[] selectedValues = list.getSelectedValues();
        for (Object selectedValue : selectedValues) {
            GeodesicPoint point = (GeodesicPoint) selectedValue;
            if (model.contains(point)) {
                continue;
            }
            model.addElement(point);
        }
    }

    private void moveUpSelectedRow(JTable src) {
        int selectedRow = src.getSelectedRow();
        dataShowModel.move((GeodesicPoint) dataShowModel.getValueAt(selectedRow, 0), -1);

        int newSelection = selectedRow > 0 ? selectedRow - 1 : 0;
        dataShowTable.getSelectionModel().setSelectionInterval(newSelection, newSelection);
    }

    private void moveDownSelectedRow(JTable src) {
        int selectedRow = src.getSelectedRow();
        dataShowModel.move((GeodesicPoint) dataShowModel.getValueAt(selectedRow, 0), +1);

        int maxvalue = dataShowTable.getRowCount() - 1;
        int newSelection = selectedRow < maxvalue ? selectedRow + 1 : maxvalue;
        dataShowTable.getSelectionModel().setSelectionInterval(newSelection, newSelection);
    }

    private void removeSelectedRow(JTable src) {
        int selectedRow = src.getSelectedRow();
        if (selectedRow >= 0) {
            DCTableModel model = (DCTableModel) src.getModel();
            model.removeElement((GeodesicPoint) model.getValueAt(selectedRow, 0));
        }
    }

    private void initListeners() {
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowOpened(WindowEvent e) {
            }
        });

        dataShowTable.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                JTable src = (JTable) e.getSource();
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    removeSelectedRow(src);
                }

                if (e.isControlDown()) {
                    e.consume();
                    if (e.getKeyCode() == KeyEvent.VK_UP) {
                        moveUpSelectedRow(src);
                    } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        moveDownSelectedRow(src);
                    }
                }
            }
        });

        allDataList.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    JList src = (JList) e.getSource();
                    copySelectedToTable(src, dataShowModel);
                }
            }
        });
        allDataList.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    JList src = (JList) e.getSource();
                    copySelectedToTable(src, dataShowModel);
                }
            }
        });
    }

    private String readCpfFromFileName(File file) {
        String name = file.getName();
        int indexOf = name.toLowerCase().indexOf("planilha_");
        if (indexOf >= 0) {
            return name.substring(9, name.length() - 4);
        }
        return "";
    }

    private Map<String, String> readMapConfig() {
        Map<String, String> mapConfig = new HashMap<String, String>();
        mapConfig.put("$NOME_DA_PLANILHA", imovelSncr.getText() + " - " + proprietarioNomeTextField.getText());
        mapConfig.put("$IMOVEL", imovelTextField.getText());
        mapConfig.put("$PROPRIETARIO", proprietarioNomeTextField.getText());
        mapConfig.put("$MATRICULA", matriculaTextField.getText());
        mapConfig.put("$SNCR_IMOVEL", imovelSncr.getText());
        mapConfig.put("$COMARCA", comarcaTextField.getText());
        mapConfig.put("$CPF_PROPRIETARIO", proprietarioCpfTextField.getText());
        mapConfig.put("$CIRCUNSCRICAO", circunscricaoTextField.getText());
        mapConfig.put("$AREA", areaCalculadaTextField.getText());
        mapConfig.put("$MUNICIPIO_UF", municipioTextField.getText());
        mapConfig.put("$DATUM", sisGeoRefTextField.getText());

        return mapConfig;
    }

    class RinexPointImporterHandle implements PointImporterHandle {

        private final DCListModel model;
        private final File file;

        public RinexPointImporterHandle(DCListModel model, File file) {
            this.model = model;
            this.file = file;
        }

        public void handlePoint(GeodesicPoint point) {
            GeodesicPoint element = model.getElement(point);
            if (element != null) {
                element.getMetaData().setRinex(file);
            }
        }

        @Override
        public void startImport() {
        }

        @Override
        public void endImport() {
        }
    }

    class ListPointImporterHandle implements PointImporterHandle {

        private DCListModel model;

        public ListPointImporterHandle(DCListModel model) {
            this.model = model;
        }

        public void handlePoint(GeodesicPoint point) {
            model.addElement(point);
        }

        @Override
        public void startImport() {
        }

        @Override
        public void endImport() {
        }
    }

    class CalculoAreaPointImporterHandle implements PointImporterHandle {

        private DCTableModel model;

        public CalculoAreaPointImporterHandle(DCTableModel model) {
            this.model = model;
        }

        public void handlePoint(GeodesicPoint p) {
            GeodesicPoint element = allDataListModel.getElement(p);
            if (element != null) {
                model.addElement(element);
            } else {
                model.addElement(p);
            }
        }

        @Override
        public void startImport() {
        }

        @Override
        public void endImport() {
        }
    }

    class RinexAnalyserDirectory extends Thread {

        private File dir;
        private DCListModel modelToImport;
        private JButton src;
        private boolean executing = false;
        private boolean canceled = false;

        public RinexAnalyserDirectory(File dir, DCListModel modelToImport, JButton src) {
            this.dir = dir;
            this.modelToImport = modelToImport;
            this.src = src;
        }

        public void execute(File dir, DCListModel modelToImport) throws IOException, InvalidGeodesicPointException {
            File[] childs = dir.listFiles();
            for (File child : childs) {
                if (canceled) {
                    return;
                }

                if (child.isDirectory()) {
                    execute(child, modelToImport);
                } else {
                    if (!child.getName().toLowerCase().matches(".+\\.(.)*\\d{2,2}o")) {
                        continue;
                    }
                    PointImporterHandle handle = new RinexPointImporterHandle(modelToImport, child);
                    GeodesicEnum[] sequence = {GeodesicEnum.NAME};
                    FileInputStream fis = new FileInputStream(child);
                    try {
                        new RinexImporter(handle, fis, sequence, utmZone, hemisphere, datum).importData();
                        rinexDirectoryTextField.setText(dir.getAbsolutePath());
                    } finally {
                        fis.close();
                    }
                }
            }
        }

        public void cancel() {
            this.canceled = true;
        }

        public boolean isExecuting() {
            return executing;
        }

        @Override
        public void run() {
            String oString = src.getText();
            src.setText("Cancelar");
            analisingRinexLabel.setText("Analisando diretório de arquivos rinex.");

            try {
                executing = true;
                execute(dir, modelToImport);
            } catch (Exception ex) {
                Logger.getLogger(DCGenerator.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Ocorreu um erro na análise dos arquivos rinex");
            } finally {
                src.setText(oString);
                analisingRinexLabel.setText("");
                executing = false;
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        dadosProjetoPanel = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel6 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        comarcaTextField = new javax.swing.JTextField();
        matriculaTextField = new javax.swing.JTextField();
        imovelTextField = new javax.swing.JTextField();
        municipioTextField = new javax.swing.JTextField();
        circunscricaoTextField = new javax.swing.JTextField();
        jPanel7 = new javax.swing.JPanel();
        proprietarioCpfTextField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        imovelSncr = new javax.swing.JTextField();
        sisGeoRefTextField = new javax.swing.JTextField();
        areaCalculadaTextField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        proprietarioNomeTextField = new javax.swing.JTextField();
        jButton11 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        fileSourcesList = new javax.swing.JList();
        jLabel13 = new javax.swing.JLabel();
        findButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        rinexDirectoryTextField = new javax.swing.JTextField();
        addRinexDirectoryButton = new javax.swing.JButton();
        analisingRinexLabel = new javax.swing.JLabel();
        jButton17 = new javax.swing.JButton();
        gerenciarPontosPanel = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        opcaoGerarCombo = new javax.swing.JComboBox();
        jButton5 = new javax.swing.JButton();
        conversaoPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jLabel16 = new javax.swing.JLabel();
        jCheckBox1 = new javax.swing.JCheckBox();
        dirOutTextField = new javax.swing.JTextField();
        jButton18 = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        sobrePanel = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        serialMotherBoardLabel = new javax.swing.JLabel();
        serialMotherBoardLabel1 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Planilha de Resultados - Dados Cartográficos");

        jSplitPane1.setBorder(javax.swing.BorderFactory.createTitledBorder("Informações do cabeçalho"));

        jLabel6.setFont(new java.awt.Font("Ubuntu", 0, 11));
        jLabel6.setText("Circunscricão:");

        jLabel7.setFont(new java.awt.Font("Ubuntu", 0, 11));
        jLabel7.setText("Município:");

        jLabel4.setFont(new java.awt.Font("Ubuntu", 0, 11));
        jLabel4.setText("Número da(s) matrícula:");

        jLabel5.setFont(new java.awt.Font("Ubuntu", 0, 11));
        jLabel5.setText("Comarca:");

        jLabel2.setFont(new java.awt.Font("Ubuntu", 0, 11));
        jLabel2.setText("Denominação do imóvel:");

        jButton6.setText("...");
        jButton6.setFocusable(false);

        jButton7.setText("...");
        jButton7.setFocusable(false);

        jButton8.setText("...");
        jButton8.setFocusable(false);

        jButton9.setText("...");
        jButton9.setFocusable(false);

        jButton10.setText("...");
        jButton10.setFocusable(false);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(matriculaTextField)
                    .addComponent(comarcaTextField)
                    .addComponent(circunscricaoTextField)
                    .addComponent(municipioTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(imovelTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton10))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {circunscricaoTextField, comarcaTextField, imovelTextField, matriculaTextField, municipioTextField});

        jPanel6Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton10, jButton6, jButton7, jButton8, jButton9});

        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(imovelTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(jButton6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(matriculaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jButton7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comarcaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jButton8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(circunscricaoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jButton9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(municipioTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jButton10))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(jPanel6);

        jLabel8.setFont(new java.awt.Font("Ubuntu", 0, 11));
        jLabel8.setText("Nome do Proprietário:");

        jLabel9.setFont(new java.awt.Font("Ubuntu", 0, 11));
        jLabel9.setText("Código(s) do SNCR do imóvel:");

        jButton14.setText("...");
        jButton14.setFocusable(false);

        jButton15.setText("...");
        jButton15.setFocusable(false);

        jLabel10.setFont(new java.awt.Font("Ubuntu", 0, 11));
        jLabel10.setText("CPF/CNPJ do Proprietário:");

        jLabel11.setFont(new java.awt.Font("Ubuntu", 0, 11));
        jLabel11.setText("Área calculada:");

        jLabel12.setFont(new java.awt.Font("Ubuntu", 0, 11));
        jLabel12.setText("Sistema Geodésico de Referência:");

        jButton11.setText("...");
        jButton11.setFocusable(false);

        jButton13.setText("...");
        jButton13.setFocusable(false);

        jButton12.setText("...");
        jButton12.setFocusable(false);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9)
                    .addComponent(jLabel10)
                    .addComponent(jLabel11)
                    .addComponent(jLabel12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(imovelSncr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton12))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(proprietarioNomeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 251, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(proprietarioCpfTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton13))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(areaCalculadaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton14))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(sisGeoRefTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton15)))
                .addContainerGap(40, Short.MAX_VALUE))
        );

        jPanel7Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {areaCalculadaTextField, imovelSncr, proprietarioCpfTextField, proprietarioNomeTextField, sisGeoRefTextField});

        jPanel7Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton11, jButton12, jButton13, jButton14, jButton15});

        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(proprietarioNomeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jButton11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(imovelSncr, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jButton12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(proprietarioCpfTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(jButton13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(areaCalculadaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11)
                    .addComponent(jButton14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sisGeoRefTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12)
                    .addComponent(jButton15))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel7);

        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Arquivos de recursos"));

        fileSourcesList.setModel(new DefaultListModel());
        jScrollPane1.setViewportView(fileSourcesList);

        jLabel13.setText("Adicione aqui todos os arquivos resultantes do pos processamento.");

        findButton.setText("Carregar arquivo");
        findButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                findButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Diretório dos arquivos rinex");

        rinexDirectoryTextField.setEditable(false);

        addRinexDirectoryButton.setText("Procurar");
        addRinexDirectoryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRinexDirectoryButtonActionPerformed(evt);
            }
        });

        analisingRinexLabel.setText("                  ");

        jButton17.setText("ler da Área de Transferência");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 946, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(findButton, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton17))
                    .addComponent(jLabel13)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(analisingRinexLabel)
                            .addComponent(rinexDirectoryTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 617, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(addRinexDirectoryButton, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(findButton)
                    .addComponent(jButton17))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(rinexDirectoryTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addRinexDirectoryButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(analisingRinexLabel)
                .addContainerGap())
        );

        javax.swing.GroupLayout dadosProjetoPanelLayout = new javax.swing.GroupLayout(dadosProjetoPanel);
        dadosProjetoPanel.setLayout(dadosProjetoPanelLayout);
        dadosProjetoPanelLayout.setHorizontalGroup(
            dadosProjetoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dadosProjetoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dadosProjetoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 982, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        dadosProjetoPanelLayout.setVerticalGroup(
            dadosProjetoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dadosProjetoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Dados do Projeto", dadosProjetoPanel);

        jSplitPane2.setDividerLocation(200);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Pontos disponíveis"));
        jPanel2.setLayout(new javax.swing.BoxLayout(jPanel2, javax.swing.BoxLayout.LINE_AXIS));
        jSplitPane2.setLeftComponent(jPanel2);

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));
        jSplitPane2.setRightComponent(jPanel1);

        jButton1.setText("Remover");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton16.setText("Remover tudo");
        jButton16.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton16ActionPerformed(evt);
            }
        });

        jButton2.setText("Mover para cima");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Mover para baixo");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("pelo Cálculo Área");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        opcaoGerarCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "INCRA", "Terra Legal", "Terra Legal MEMO" }));

        jButton5.setText("Gerar planilha");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton16, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 132, Short.MAX_VALUE)
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(opcaoGerarCombo, 0, 140, Short.MAX_VALUE)
                        .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 140, Short.MAX_VALUE))))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 372, Short.MAX_VALUE)
                .addComponent(opcaoGerarCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton5))
        );

        javax.swing.GroupLayout gerenciarPontosPanelLayout = new javax.swing.GroupLayout(gerenciarPontosPanel);
        gerenciarPontosPanel.setLayout(gerenciarPontosPanelLayout);
        gerenciarPontosPanelLayout.setHorizontalGroup(
            gerenciarPontosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, gerenciarPontosPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 824, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        gerenciarPontosPanelLayout.setVerticalGroup(
            gerenciarPontosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(gerenciarPontosPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(gerenciarPontosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 636, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Gerenciar Pontos", gerenciarPontosPanel);

        jLabel3.setText("Converter arquivo txt para a planilha de validação do Terra Legal");

        jList1.setModel(fileModel);
        jScrollPane2.setViewportView(jList1);

        jLabel16.setText("Diretório de saída:");

        jCheckBox1.setText("Mesmo diretório");
        jCheckBox1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox1StateChanged(evt);
            }
        });

        dirOutTextField.setEditable(false);

        jButton18.setText("Adicionar arquivo(s)");
        jButton18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton18ActionPerformed(evt);
            }
        });

        jButton19.setText("Converter");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton20.setText("Procurar");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 715, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel16))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jCheckBox1))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton18, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 301, Short.MAX_VALUE)
                .addComponent(jButton19, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 691, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(dirOutTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 503, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton20, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton18)
                    .addComponent(jButton19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(jLabel16)
                .addGap(6, 6, 6)
                .addComponent(jCheckBox1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dirOutTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton20))
                .addContainerGap())
        );

        javax.swing.GroupLayout conversaoPanelLayout = new javax.swing.GroupLayout(conversaoPanel);
        conversaoPanel.setLayout(conversaoPanelLayout);
        conversaoPanelLayout.setHorizontalGroup(
            conversaoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(conversaoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(conversaoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(279, Short.MAX_VALUE))
        );
        conversaoPanelLayout.setVerticalGroup(
            conversaoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(conversaoPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(166, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Converter", conversaoPanel);

        jLabel14.setText("Versão do programa:");

        serialMotherBoardLabel.setText("1.1.8");

        serialMotherBoardLabel1.setText("29/04/2010 - 15:19");

        jLabel15.setText("Data de compilação:");

        javax.swing.GroupLayout sobrePanelLayout = new javax.swing.GroupLayout(sobrePanel);
        sobrePanel.setLayout(sobrePanelLayout);
        sobrePanelLayout.setHorizontalGroup(
            sobrePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sobrePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sobrePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(sobrePanelLayout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(serialMotherBoardLabel))
                    .addGroup(sobrePanelLayout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(serialMotherBoardLabel1)))
                .addContainerGap(700, Short.MAX_VALUE))
        );
        sobrePanelLayout.setVerticalGroup(
            sobrePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sobrePanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(sobrePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(serialMotherBoardLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(sobrePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(serialMotherBoardLabel1))
                .addContainerGap(602, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Sobre", sobrePanel);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1014, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 37, Short.MAX_VALUE)
        );

        getContentPane().add(jPanel3, java.awt.BorderLayout.SOUTH);

        java.awt.Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((screenSize.width-1014)/2, (screenSize.height-768)/2, 1014, 768);
    }// </editor-fold>//GEN-END:initComponents

    private void findButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_findButtonActionPerformed
        File file = FileFinder.selectFileToOpenUI(this, FileFinder.GPS_SOURCE_EXTENSIONS);
        if (file == null) {
            return;
        }

        try {
            importDataToList(file, allDataListModel);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DCGenerator.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "O arquivo " + file + " não foi encontrado.");
        } catch (IOException ex) {
            Logger.getLogger(DCGenerator.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Falha ao tentar ler o conteúdo do arquivo: " + file + "\n." + ex.getMessage());
        } catch (InvalidGeodesicPointException ex) {
            Logger.getLogger(DCGenerator.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "O arquivo " + file + " possui pontos inválidos ou não está na ordem correta.\n\nMais informacao: \n" + ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(DCGenerator.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, ex.getMessage());
        }
}//GEN-LAST:event_findButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        removeSelectedRow(dataShowTable);
}//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        moveUpSelectedRow(dataShowTable);
}//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        moveDownSelectedRow(dataShowTable);
}//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        int rowCount = dataShowTable.getRowCount();
        int confirm = 0;
        if (rowCount > 0) {
            confirm = JOptionPane.showConfirmDialog(this, "Organizar os pontos pelo cálculo de área irá perder a organizacao atual realizada, tem certeza que deseja prosseguir?", "Pergunta", JOptionPane.OK_CANCEL_OPTION);
        }

        if (rowCount == 0 || confirm == JOptionPane.OK_OPTION) {
            FileFinder.ExtensionFileFilter fileFilter = new FileFinder.ExtensionFileFilter("rtf", "xlsx", "xls");
            File file = FileFinder.selectFileToOpenUI(this, fileFilter);
            if (file == null) {
                return;
            }

            dataShowModel.clearAll();

            try {
                importDataToTable(file, dataShowModel);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(DCGenerator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(DCGenerator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidGeodesicPointException ex) {
                Logger.getLogger(DCGenerator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(DCGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
}//GEN-LAST:event_jButton4ActionPerformed

    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton16ActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, "Tem certeza que deseja limpar a tabela de pontos?", "Pergunta", JOptionPane.OK_CANCEL_OPTION);
        if (confirm == JOptionPane.OK_OPTION) {
            dataShowModel.clearAll();
        }
}//GEN-LAST:event_jButton16ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        Map<String, String> mapConfig = readMapConfig();

        try {
            File outFile = File.createTempFile("pc9_", ".ods");
            outFile.deleteOnExit();
            if (opcaoGerarCombo.getSelectedIndex() == 0) {
                dcIncraOds.generate(outFile, mapConfig, dataShowModel.getList());
            } else if (opcaoGerarCombo.getSelectedIndex() == 1) {
                dcTerraLegalOds.generate(outFile, mapConfig, dataShowModel.getList());
            } else if (opcaoGerarCombo.getSelectedIndex() == 2) {
                memoTerraLegalOds.generate(outFile, mapConfig, dataShowModel.getList());
            } else if (opcaoGerarCombo.getSelectedIndex() == 3) {
                terraLegalValidacaoOds.generate(outFile, mapConfig, dataShowModel.getList());
            }

            JOptionPane.showMessageDialog(this, "A planilha foi gerada com sucesso.\n"
                    + "Clique em OK para abrir a planilha no BrOffice.\n"
                    + "Após abrir a planilha, clique em salvar como.");


            Desktop desktop = Desktop.getDesktop();
            desktop.open(outFile);

            System.out.println(outFile);
        } catch (IOException ex) {
            Logger.getLogger(DCGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
}//GEN-LAST:event_jButton5ActionPerformed

    private void addRinexDirectoryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRinexDirectoryButtonActionPerformed
        DefaultListModel model = (DefaultListModel) fileSourcesList.getModel();
        if (model.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Primeiro adicione os arquivos de recursos.");
            return;
        }

        JButton src = (JButton) evt.getSource();

        if (rinexAnalyser == null || !rinexAnalyser.isExecuting()) {
            File file = FileFinder.selectDirToOpenUI(this);
            if (file == null) {
                return;
            }

            rinexAnalyser = new RinexAnalyserDirectory(file, allDataListModel, src);
            rinexAnalyser.start();
        } else {
            rinexAnalyser.cancel();
        }
    }//GEN-LAST:event_addRinexDirectoryButtonActionPerformed

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        File file = null;
        try {
            String currentClipboardValue = MiscUtils.currentClipboardValue();
            file = new File(new URI(currentClipboardValue));
            importDataToList(file, allDataListModel);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DCGenerator.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "O arquivo " + file + " não foi encontrado.");
        } catch (IOException ex) {
            Logger.getLogger(DCGenerator.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Falha ao tentar ler o conteúdo do arquivo: " + file + "\n." + ex.getMessage());
        } catch (InvalidGeodesicPointException ex) {
            Logger.getLogger(DCGenerator.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "O arquivo " + file + "possui pontos inválidos ou não está na ordem correta.\n\nMais informacao: \n" + ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(DCGenerator.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex.getMessage());
        }

    }//GEN-LAST:event_jButton17ActionPerformed

    private void jCheckBox1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox1StateChanged
        JToggleButton src = (JToggleButton) evt.getSource();

        dirOutTextField.setEnabled(!src.isSelected());
        jButton20.setEnabled(!src.isSelected());
}//GEN-LAST:event_jCheckBox1StateChanged

    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton18ActionPerformed
        File[] selectedFiles = FileFinder.selectFilesToOpenUI(this, FileFinder.GPS_SOURCE_EXTENSIONS);
        if (selectedFiles != null) {
            for (File file : selectedFiles) {
                fileModel.addElement(file);
            }
        }
}//GEN-LAST:event_jButton18ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        boolean sameDir = jCheckBox1.isSelected();

        for (int i = 0; i < fileModel.getSize(); i++) {
            File file = (File) fileModel.getElementAt(i);

//            FileOutputStream out = null;
            FileInputStream input = null;
            try {
                File dirOut = sameDir ? file.getParentFile() : new File(dirOutTextField.getText());
                File fileOut = new File(dirOut, file.getName().substring(0, file.getName().length() - 4) + ".ods");
                input = new FileInputStream(file);
//                out = new FileOutputStream(fileOut);

                PointImporterListHandler handler = new PointImporterListHandler();
                CSVPointImporter importer = new CSVPointImporter(handler, input, txtOrder, ";", "iso-8859-1", utmZone, hemisphere, datum);
                importer.importData();

                Map<String, String> mapConfig = readMapConfig();
                String cpfProprietario = mapConfig.get("$CPF_PROPRIETARIO");
                if (cpfProprietario == null || cpfProprietario.equals("")) {
                    mapConfig.put("$CPF_PROPRIETARIO", readCpfFromFileName(file));
                }
                terraLegalValidacaoOds.generate(fileOut, mapConfig, handler.getPoints());
            } catch (Exception ex) {
                ex.printStackTrace();
                String message = "Erro na conversão do arquivo: " + file.getName() + "\n" + ex.getMessage() + "\n\nDeseja continuar tentando converter os outros arquivos?";
                int result = JOptionPane.showConfirmDialog(this, message, "Pergunta", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.NO_OPTION) {
                    return;
                }
            } finally {
                try {
//                    out.close();
                } catch (Exception e) {
                }

                try {
                    input.close();
                } catch (Exception e) {
                }
            }
        }
        JOptionPane.showMessageDialog(this, "A conversão foi concluída com sucesso.");
}//GEN-LAST:event_jButton19ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        File dir = FileFinder.selectDirToOpenUI(this);
        if (dir != null) {
            dirOutTextField.setText(dir.getName());
        }
}//GEN-LAST:event_jButton20ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                System.out.println(System.currentTimeMillis());
//                try {
////                    UIManager.setLookAndFeel(new PC9LookAndFeel());
//                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//                } catch (Exception ex) {
//                    Logger.getLogger(DCGenerator.class.getName()).log(Level.SEVERE, null, ex);
//                }
//
//                System.out.println(System.currentTimeMillis());
//                String inicio = Defensor.getProperty("inicio");
//                Date currentDate;
//                Calendar endDate = Calendar.getInstance();
//                try {
//                    endDate.setTimeInMillis(Long.parseLong(inicio));
//
//                    endDate.add(Calendar.MONTH, 3);
//                    System.out.println(endDate.getTime());
//
//                    currentDate = MiscUtils.currentDateTimeFromWeb();
//                } catch (Exception ex) {
//                    currentDate = new Date();
//                    Logger.getLogger(DCGenerator.class.getName()).log(Level.SEVERE, null, ex);
//                }
//                if (currentDate.after(endDate.getTime())) {
//                    javax.swing.JOptionPane.showMessageDialog(null, "Programa gerador de dados cartográficos.\nPeríodo de avaliação expirou.");
//                } else {
//
//                    new DCGenerator().setVisible(true);
//
//                }

                try {
//                    UIManager.setLookAndFeel(new PC9LookAndFeel());
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ex) {
                    Logger.getLogger(DCGenerator.class.getName()).log(Level.SEVERE, null, ex);
                }

                try {
                    if (SecurityUtils.verifyRegister()) {
                        new DCGenerator().setVisible(true);
                    } else {
                        String keyEntered = SecurityUtils.showInputKeyRegister();
                        if (SecurityUtils.register(keyEntered)) {
                            new DCGenerator().setVisible(true);
                        } else {
                            JOptionPane.showMessageDialog(null, "Chave inválida.\nO programa será fechado.");
                            System.exit(0);
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(SecurityUtils.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "Falha na leitura do disco...");
                }
            }
        });

    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addRinexDirectoryButton;
    private javax.swing.JLabel analisingRinexLabel;
    private javax.swing.JTextField areaCalculadaTextField;
    private javax.swing.JTextField circunscricaoTextField;
    private javax.swing.JTextField comarcaTextField;
    private javax.swing.JPanel conversaoPanel;
    private javax.swing.JPanel dadosProjetoPanel;
    private javax.swing.JTextField dirOutTextField;
    private javax.swing.JList fileSourcesList;
    private javax.swing.JButton findButton;
    private javax.swing.JPanel gerenciarPontosPanel;
    private javax.swing.JTextField imovelSncr;
    private javax.swing.JTextField imovelTextField;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField matriculaTextField;
    private javax.swing.JTextField municipioTextField;
    private javax.swing.JComboBox opcaoGerarCombo;
    private javax.swing.JTextField proprietarioCpfTextField;
    private javax.swing.JTextField proprietarioNomeTextField;
    private javax.swing.JTextField rinexDirectoryTextField;
    private javax.swing.JLabel serialMotherBoardLabel;
    private javax.swing.JLabel serialMotherBoardLabel1;
    private javax.swing.JTextField sisGeoRefTextField;
    private javax.swing.JPanel sobrePanel;
    // End of variables declaration//GEN-END:variables
}
