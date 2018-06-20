/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package userentry;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;

/**
 *
 * @author RIDDHI DUTTA
 */

public class UserCRUDFrame extends javax.swing.JFrame 
{
    private int curID=-1;
    private char mode='n'; // n= navigating mode, e= edit mode , a= add mode
    private Map<Character,JRadioButton> mapGender= new HashMap<>();
    private Map<Character,JRadioButton> mapMarital= new HashMap<>();
    private ArrayList<ArrayList<String>> cities= new ArrayList<>();
    private ArrayList<String> states= new ArrayList<>();
    private  JFileChooser chooser = new JFileChooser("pics");
    private Image imgUser;
    public static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
    public static final String userFileName = "users.txt";
    public static final String imagesFileName = "UserImages.dat";
    private DefaultListModel<Item> emailModel = new DefaultListModel<>();
    private DefaultListModel<Item> idModel = new DefaultListModel<>();
    private DefaultListModel<Item> hobbyModel = new DefaultListModel<>();
    
    public void enableComponents(boolean b)
    {
       tfAdd1.setEnabled(b);
       tfAdd2.setEnabled(b);
       tfAdd3.setEnabled(b);
       tfName.setEnabled(b);
       tfEmail.setEnabled(b);
       tfPhn.setEnabled(b);
       tfDob.setEnabled(b);
      
       tfUserId.setEnabled(true);
       tfUserId.setEditable(false);
       
       taRemarks.setEnabled(b);
       radOthers.setEnabled(b);
       radFemale.setEnabled(b);
       radMale.setEnabled(b);
       radMarried.setEnabled(b);
       radSeperated.setEnabled(b);
       radBachelor.setEnabled(b);
       cmbState.setEnabled(b);
       cmbCity.setEnabled(b);
       chkCar.setEnabled(b);
       chkHome.setEnabled(b);
       btnRemove.setEnabled(b);
       btnUpload.setEnabled(b);
       
       boolean c = curID!=-1;
       btnSave.setEnabled(b);
       btnCancel.setEnabled(b);
       btnFirst.setEnabled(!b&&c);
       btnLast.setEnabled(!b&&c);
       btnNext.setEnabled(!b&&c);
       btnPrev.setEnabled(!b&&c);
       btnUpdate.setEnabled(!b&&c);
       btnNew.setEnabled(!b);
       btnDelete.setEnabled(!b&&c);
       radEmail.setEnabled(!b&&c);
       radCode.setEnabled(!b&&c);
       lst.setEnabled(!b&&c);
    }
    
    public void blankComponents()
    {
        tfAdd1.setText("");
        tfAdd2.setText("");
        tfAdd3.setText("");
        tfEmail.setText("");
        tfName.setText("");
        tfPhn.setText("");
        taRemarks.setText("");
        tfUserId.setText("");
        radOthers.setSelected(true);
        radBachelor.setSelected(true);
        cmbState.setSelectedIndex(0);
        cmbCity.setSelectedIndex(0);
        chkCar.setSelected(false);
        chkHome.setSelected(false);
        imgUser = null;
        lblPicture.setIcon(null);
        tfDob.setText("");
        
        lstHobbies.clearSelection();
    }
    
    public boolean isTextFieldBlank(JTextField tf)
    {
        return tf.getText().trim().length()==0;
    }
    public boolean isEmailUnique(String email)
    {
        if(mode=='a')
        {
            for(int i=0;i<emailModel.size();i++)
                if(email.equals(emailModel.getElementAt(i).toString()))
                    return false;
            
            return true;    
        }
        if(mode=='e')
        {
           if(email.equals(emailModel.getElementAt(lst.getSelectedIndex()).toString()))
           {
               return true;
           }
            for(int i=0;i<emailModel.size();i++)
                if(email.equals(emailModel.getElementAt(i).toString()))
                    return false;
        }
        return true; //for edit mode.
    }
    public boolean checkValidData()
    {
        /* checking for name */
        if(isTextFieldBlank(tfName))
        {
            JOptionPane.showMessageDialog(this, "Name cannot be Blank!", "Blank Name", JOptionPane.WARNING_MESSAGE);
            tfName.requestFocus();
            return false;
        }
        if(!isEmailUnique(tfEmail.getText().trim()))
        {
            JOptionPane.showMessageDialog(this, "Email must be unique!", "Duplicate Email", JOptionPane.WARNING_MESSAGE);
            tfEmail.requestFocus();
            return false;
        }
        return true;
    }
    
    public void readStateCap() throws FileNotFoundException
    {
        Scanner sc= new Scanner(new FileInputStream("Statewise List of Cities in India.txt")); 
        String prevState= "";
        ArrayList<String> cityTemp= null;
        sc.nextLine(); //skip the header
        while(sc.hasNextLine())
        {
            String s= sc.nextLine();
            String st= s.substring(43);
            st= st.trim();
            String city= s.substring(0,43);
            city= city.trim();
            if(!prevState.equals(st))
            {
                states.add(st);
                cityTemp= new ArrayList<>();
                cities.add(cityTemp);
            }
            cityTemp.add(city);
            prevState= st;
        }
        int i=0;
        for (String state : states)
            cmbState.addItem(state);
    }
     
    public void fetchFromUser(User user) // fill the components of the screen with a data of the given user.
    {
        tfName.setText(user.getName());
        tfUserId.setText(user.getUserID());
        tfAdd1.setText(user.getAddr1());
        tfAdd2.setText(user.getAddr2());
        tfAdd3.setText(user.getAddr3());
        
        tfEmail.setText(user.getEmail());
        tfPhn.setText(user.getPhoneNo());
        taRemarks.setText(user.getRemarks());
        
        cmbState.setSelectedIndex(user.getStateNo());
        cmbCity.setSelectedIndex(user.getCityNo());
        
        mapGender.get(user.getGender()).setSelected(true);
        mapMarital.get(user.getMaritalStatus()).setSelected(true);
        
        chkCar.setSelected(user.isHasCar());
        chkHome.setSelected(user.isHasHome());
        
       imgUser = user.getImage();
        if(imgUser!=null)
            lblPicture.setIcon(new ImageIcon(imgUser));
        else
            lblPicture.setIcon(null);
        
        tfDob.setText(sdf.format(user.getDt()));
        
        
//        imgUser = loadImage(user.getPicinfo());
//        if(imgUser==null)
//            lblPicture.setIcon(null);
//        else
//            fitImageToLabel(imgUser, lblPicture);
    }
    
    private static void fitImageToLabel(Image img, JLabel lbl)
    {
        Image simg = img.getScaledInstance(lbl.getWidth(), lbl.getHeight(), Image.SCALE_SMOOTH);
        lbl.setIcon(new ImageIcon(simg));
    }
    
     public void storeToResultSet(Connection con,ResultSet rs,int id)
    {
        try
        {
                rs.updateInt("id",id);
        rs.updateString("name",tfName.getText().trim());
        if(imgUser!=null)
        {
            Blob b = con.createBlob();
            OutputStream out = b.setBinaryStream(0);
            ImageIO.write((BufferedImage)imgUser,"JPG",out);
            rs.updateBlob("pic", b);
        }
        rs.updateString("Address1",tfAdd1.getText().trim());
        rs.updateString("Address2",tfAdd2.getText().trim());
        rs.updateString("Address3",tfAdd3.getText().trim());
        rs.updateInt("cityid",cmbCity.getSelectedIndex());
        rs.updateInt("stateid",cmbState.getSelectedIndex());
        rs.updateString("remarks",taRemarks.getText());
        rs.updateDate("DOB",(new java.sql.Date(sdf.parse(tfDob.getText().trim()).getTime())));
        rs.updateString("email",tfEmail.getText().trim());
        rs.updateString("phno",tfPhn.getText().trim());
        rs.updateString("code",tfUserId.getText().trim());
       
        char gnd='N';
        if(radMale.isSelected())
           gnd = 'M';
        else if(radFemale.isSelected())
            gnd = 'F';
        
        rs.updateString("gender",gnd+"");
        
        char mar = 'S';
        if(radMarried.isSelected())
            mar='M';
        else if(radBachelor.isSelected())
            mar = 'U';
        
        rs.updateString("marital",mar+"");
        
        rs.updateString("hasCar",chkCar.isSelected()?"Y":"N");
        rs.updateString("hasHome",chkHome.isSelected()?"Y":"N");
        
        }
        catch(Exception e)
        { 
             Logger.getLogger(UserCRUDFrame.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private User createUser(Connection con,ResultSet rs)
    {
        try {
            boolean hasCar = rs.getString("hasCar").equals("Y");
            boolean hasHome = rs.getString("hasHome").equals("Y");
            User user = new User(rs.getString("Code"),rs.getString("name"),rs.getString("address1"),rs.getString("address2"),
                    rs.getString("address3"),rs.getDate("DOB"),rs.getString("gender").charAt(0),rs.getString("marital").charAt(0),
                    rs.getInt("Stateid"),rs.getInt("Cityid"),rs.getString("phno"),rs.getString("email"),rs.getString("remarks"),
                    hasCar,hasHome);
           Blob b = rs.getBlob("Pic");
           if(rs.wasNull())
               return user;
           
           InputStream in = b.getBinaryStream();
            try {
                imgUser = ImageIO.read(in);
            } catch (IOException ex) {
                Logger.getLogger(UserCRUDFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
           
           user.setImage(imgUser);
            return user;
        } catch (SQLException ex) {
            Logger.getLogger(UserCRUDFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public void fetchData()  //fetches data from the user with curIndex.
    {
        blankComponents();
        if(lst.getSelectedIndex() == -1)
            return ;
        curID = emailModel.getElementAt(lst.getSelectedIndex()).getId();
        System.out.println("fetchdata");
        try {
                Connection con = Main.db.createConnection();
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("select * from myusers where id="+curID);
                if(rs.next())
                    fetchFromUser(createUser(con,rs));
                
                stmt = con.createStatement();
                rs = stmt.executeQuery("select HobbyId from UserHobbies where UserID="+curID);
                while(rs.next())
                {
                    int id = rs.getInt(1);
                    for(int i=0;i<hobbyModel.size();i++)
                        if(hobbyModel.getElementAt(i).getId()==id)
                        {
                            lstHobbies.setSelectedIndex(i);
                            break;
                        }
                }
                con.close();
            } catch (SQLException ex) {
                Logger.getLogger(UserCRUDFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    public UserCRUDFrame()
    {
           try {
               initComponents();
                lstHobbies.setModel(hobbyModel);
                        // === Makes more than one check box in a list box checked at a time === //
            lstHobbies.setSelectionModel(new DefaultListSelectionModel() 
            {
                @Override
                public void setSelectionInterval(int index0, int index1) 
                {
                    if(super.isSelectedIndex(index0)) 
                        super.removeSelectionInterval(index0, index1);
                    else
                        super.addSelectionInterval(index0, index1);
                }
            });
            // ===================================================================== //
           lstHobbies.setCellRenderer(new CheckboxListCellRenderer());

        /* adding hobbies to list */
        
        Connection con = Main.db.createConnection();
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select * from Hobby");
        
        while(rs.next())
            hobbyModel.addElement(new Item(rs.getString(2),rs.getInt(1)));
        
        
               try {
                   readStateCap();
               } catch (FileNotFoundException ex) {
                   System.out.println(ex);
               }
               sdf.setLenient(false);
               mapGender.put('M', radMale);
               mapGender.put('F', radFemale);
               mapGender.put('N', radOthers);
               mapMarital.put('M', radMarried);
               mapMarital.put('U', radBachelor);
               mapMarital.put('S', radSeperated);
               

               stmt = con.createStatement();
               rs = stmt.executeQuery("Select id,code,email from Myusers");
               
            /* ------------- adding to the model --------------------*/
            while(rs.next())
            {
                idModel.addElement(new Item(rs.getString("Code"),rs.getInt("id")));
                emailModel.addElement(new Item(rs.getString("email"),rs.getInt("id")));
            }
            curID = (emailModel.size()==0)?-1:emailModel.get(0).getId();
            if(curID!=-1)
                lst.setSelectedIndex(0);

            /*--------------- added to model -----------------*/
            
            con.close();
            
            radCode.setSelected(true);
            lst.setModel(idModel);
            lst.setSelectedIndex(curID);
            
            enableComponents(false);
            fetchData();
            
             Document doc = tfPhn.getDocument();
        
            ((AbstractDocument)doc).setDocumentFilter(new NumericDocumentFilter()); 
            
            /* Convert the content of the name to the Proper case. */
            
            
            tfName.setInputVerifier(new InputVerifier() { //online checking
                   @Override
                   public boolean verify(JComponent input)
                   {
                       if(!tfName.getText().isEmpty())
                            tfName.setText(properCase(tfName.getText()));
                       return true; // always valid
                   }
               });
            
            
            
            
        } catch (Exception ex) {
               Logger.getLogger(UserCRUDFrame.class.getName()).log(Level.SEVERE, null,ex);
        }
    }
     
    private String properCase(String text)
    {
        String words[] = text.split("\\s");
        if(words.length==0)
           return "";
        StringBuilder sb  = new StringBuilder();
        
        for(int i=0;i<words.length;i++)
        {
            if(words[i].length()>0)
                sb.append(' ').append(Character.toUpperCase(words[i].charAt(0))).append(words[i].substring(1).toLowerCase());
        }
        return sb.toString().substring(1);
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        grpGender = new javax.swing.ButtonGroup();
        grpMarital = new javax.swing.ButtonGroup();
        grpLst = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        tfUserId = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        tfName = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        radMale = new javax.swing.JRadioButton();
        radFemale = new javax.swing.JRadioButton();
        radOthers = new javax.swing.JRadioButton();
        jLabel5 = new javax.swing.JLabel();
        radMarried = new javax.swing.JRadioButton();
        radBachelor = new javax.swing.JRadioButton();
        radSeperated = new javax.swing.JRadioButton();
        jLabel6 = new javax.swing.JLabel();
        tfAdd1 = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        tfAdd2 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        tfAdd3 = new javax.swing.JTextField();
        cmbState = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cmbCity = new javax.swing.JComboBox<>();
        btnFirst = new javax.swing.JButton();
        btnPrev = new javax.swing.JButton();
        btnNext = new javax.swing.JButton();
        btnLast = new javax.swing.JButton();
        btnNew = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        tfEmail = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        tfPhn = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        chkHome = new javax.swing.JCheckBox();
        chkCar = new javax.swing.JCheckBox();
        lblPicture = new javax.swing.JLabel();
        btnUpload = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        tfDob = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        taRemarks = new javax.swing.JTextArea();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lst = new javax.swing.JList<>();
        radCode = new javax.swing.JRadioButton();
        radEmail = new javax.swing.JRadioButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        lstHobbies = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("USER CRUD");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setText("User Code");

        jLabel2.setText("Name");

        jLabel3.setText("Date Of Birth");

        jLabel4.setText("Gender");

        grpGender.add(radMale);
        radMale.setText("Male");

        grpGender.add(radFemale);
        radFemale.setText("Female");

        grpGender.add(radOthers);
        radOthers.setText("Not Disclosing");

        jLabel5.setText("Marital Status");

        grpMarital.add(radMarried);
        radMarried.setText("Married");

        grpMarital.add(radBachelor);
        radBachelor.setText("Unmarried");

        grpMarital.add(radSeperated);
        radSeperated.setText("Seperated");

        jLabel6.setText("Address 1");

        tfAdd1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfAdd1ActionPerformed(evt);
            }
        });

        jLabel7.setText("Address 2");

        tfAdd2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tfAdd2ActionPerformed(evt);
            }
        });

        jLabel8.setText("Address 3");

        cmbState.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbStateActionPerformed(evt);
            }
        });

        jLabel9.setText("State");

        jLabel10.setText("City");

        cmbCity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbCityActionPerformed(evt);
            }
        });

        btnFirst.setText("First");
        btnFirst.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFirstActionPerformed(evt);
            }
        });

        btnPrev.setText("Prev");
        btnPrev.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPrevActionPerformed(evt);
            }
        });

        btnNext.setText("Next");
        btnNext.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNextActionPerformed(evt);
            }
        });

        btnLast.setText("Last");
        btnLast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLastActionPerformed(evt);
            }
        });

        btnNew.setText("New");
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });

        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setText("Delete");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        btnCancel.setText("Cancel");
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        jLabel11.setText("Email");

        tfPhn.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tfPhnKeyTyped(evt);
            }
        });

        jLabel12.setText("Phone Number");

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Do You Have?"));

        chkHome.setText("Home");

        chkCar.setText("Car");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(chkHome)
                .addGap(37, 37, 37)
                .addComponent(chkCar)
                .addContainerGap(172, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chkHome)
                    .addComponent(chkCar))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblPicture.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 51, 51)));

        btnUpload.setText("Upload...");
        btnUpload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUploadActionPerformed(evt);
            }
        });

        btnRemove.setText("Remove Image");
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        taRemarks.setColumns(20);
        taRemarks.setRows(5);
        jScrollPane1.setViewportView(taRemarks);

        jLabel13.setText("Remarks");

        lst.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                lstValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(lst);

        grpLst.add(radCode);
        radCode.setText("BY Code");
        radCode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radCodeActionPerformed(evt);
            }
        });

        grpLst.add(radEmail);
        radEmail.setText("BY EMAIL");
        radEmail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radEmailActionPerformed(evt);
            }
        });

        jScrollPane3.setViewportView(lstHobbies);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(radEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(radCode))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnNext)
                        .addGap(18, 18, 18)
                        .addComponent(btnLast)
                        .addGap(18, 18, 18)
                        .addComponent(btnFirst)
                        .addGap(26, 26, 26)
                        .addComponent(btnPrev)
                        .addGap(237, 237, 237))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.TRAILING))
                                .addGap(63, 63, 63))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel9)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel6)
                                        .addComponent(jLabel7)
                                        .addComponent(jLabel8)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel3))
                                    .addComponent(jLabel10))
                                .addGap(52, 52, 52)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(tfAdd2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(140, 140, 140)
                                .addComponent(btnRemove)
                                .addGap(27, 27, 27)
                                .addComponent(btnUpload))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(radMarried)
                                        .addGap(18, 18, 18)
                                        .addComponent(radBachelor)
                                        .addGap(50, 50, 50)
                                        .addComponent(radSeperated))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(radMale)
                                        .addGap(18, 18, 18)
                                        .addComponent(radFemale)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(radOthers))
                                    .addComponent(tfDob, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tfName, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tfUserId, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tfAdd1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblPicture, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(tfAdd3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbState, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbCity, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(248, 248, 248))))
            .addGroup(layout.createSequentialGroup()
                .addGap(239, 239, 239)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 324, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(5, 5, 5)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addGap(7, 7, 7)
                                        .addComponent(tfPhn, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel11)
                                        .addGap(52, 52, 52)
                                        .addComponent(tfEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(339, 339, 339)
                                .addComponent(btnNew)))
                        .addGap(18, 18, 18)
                        .addComponent(btnUpdate)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancel)
                        .addGap(18, 18, 18)
                        .addComponent(btnSave)
                        .addGap(18, 18, 18)
                        .addComponent(btnDelete)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(150, 150, 150))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPicture, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfUserId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(radCode))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(radEmail)
                                .addGap(12, 12, 12))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(tfDob, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(radMale)
                                .addComponent(radFemale)
                                .addComponent(radOthers)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel5)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(radMarried)
                                                .addComponent(radBachelor)
                                                .addComponent(radSeperated)))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(tfAdd1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel6))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(tfAdd2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel7)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(82, 82, 82)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(btnUpload)
                                            .addComponent(btnRemove))))
                                .addGap(16, 16, 16)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel8)
                                    .addComponent(tfAdd3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(28, 28, 28)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel9)
                                            .addComponent(cmbState, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel10)
                                            .addComponent(cmbCity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(23, 23, 23)
                                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(32, 32, 32)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel11)
                                            .addComponent(tfEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel12)
                                            .addComponent(tfPhn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(btnNew)
                                            .addComponent(btnUpdate)
                                            .addComponent(btnCancel)
                                            .addComponent(btnSave)
                                            .addComponent(btnDelete))))
                                .addGap(14, 14, 14)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel13)
                                    .addComponent(btnNext)
                                    .addComponent(btnLast)
                                    .addComponent(btnFirst)
                                    .addComponent(btnPrev))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(218, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void tfAdd2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfAdd2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfAdd2ActionPerformed

    private void cmbCityActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbCityActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbCityActionPerformed

    private void btnPrevActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPrevActionPerformed
        // TODO add your handling code here:
         int index = lst.getSelectedIndex();
         if(index!=-1 && index!=0)
          {
              lst.setSelectedIndex(index-1);
              curID = lst.getSelectedValue().getId();
              fetchData();
          }
    }//GEN-LAST:event_btnPrevActionPerformed
            
    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        // TODO add your handling code here:
        mode = 'a';
        blankComponents();
        tfUserId.setText(generateId());
        enableComponents(true);
        tfName.requestFocus();
    }//GEN-LAST:event_btnNewActionPerformed

    private void btnFirstActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFirstActionPerformed
    int index = lst.getSelectedIndex();
          if(index!=-1)
              lst.setSelectedIndex(0);
    }//GEN-LAST:event_btnFirstActionPerformed

    private void btnNextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNextActionPerformed
        // TODO add your handling code here:

          int index = lst.getSelectedIndex();
          if(index+1<emailModel.size())
          {
              lst.setSelectedIndex(index+1);
              curID = lst.getSelectedValue().getId();
              fetchData();
          }
    }//GEN-LAST:event_btnNextActionPerformed

    private void btnLastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLastActionPerformed
        // TODO add your handling code here:
        int index = lst.getSelectedIndex();
          if(index!=-1)
          {
              lst.setSelectedIndex(emailModel.size()-1);
          }
    }//GEN-LAST:event_btnLastActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
        if(checkValidData())
        {
            try {
                Connection con = Main.db.createConnection();
                con.setAutoCommit(false);
                Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = stmt.executeQuery("select ID,Code,Address1,Address2,Address3,Name,DOB,StateID,CityID,Gender,Marital,PhNo,Email,Remarks,hasCar,hasHome,pic from MyUsers where id = "+curID);
                if(mode=='a') //add mode
                {
                    Statement stmt2 = con.createStatement();
                    ResultSet rs2 = stmt2.executeQuery("select seqId.nextVal from dual");
                    rs2.next();
                    curID = rs2.getInt(1);
                    rs.moveToInsertRow();
                    storeToResultSet(con,rs,curID);
                    rs.insertRow();
                    emailModel.addElement(new Item(tfEmail.getText().trim(),curID));
                    idModel.addElement(new Item(tfUserId.getText().trim(),curID));
                }
                else if(mode=='e') //edit mode
                {
                    /*deleting all hobbies for this user*/
                    Statement stmt2 = con.createStatement();
                    stmt2.executeUpdate("delete from UserHobbies where userid="+curID);
                    
                    rs.next();
                    storeToResultSet(con,rs, curID);
                    rs.updateRow();
                    emailModel.setElementAt(new Item(tfEmail.getText().trim(),curID), lst.getSelectedIndex());
                }
                   /*----------Save Hobbies-------------*/
                    int id = -1;
                    PreparedStatement pstmt3 = con.prepareStatement("insert into UserHobbies(UserID,HobbyID) values(?,?)");
                    for(int i=0;i<hobbyModel.size();i++)
                    {
                        if(lstHobbies.isSelectedIndex(i))
                        {
                            id = hobbyModel.getElementAt(i).getId();
                            pstmt3.setInt(1, curID);
                            pstmt3.setInt(2,id);
                            pstmt3.executeUpdate();
                        }    
                    }    
                    /*----------------------------------*/
                if(mode=='a')
                {
                    lst.setSelectedIndex(emailModel.size()-1);
                    JOptionPane.showMessageDialog(this,"Data Added ");
                }
                else if(mode=='e')
                     JOptionPane.showMessageDialog(this,"Data Updated ");
                    
                mode = 'n'; //navigating mode
                con.commit();
                con.setAutoCommit(true);
                con.close();
                fetchData();
                enableComponents(false);
            } catch (SQLException ex) {
                Logger.getLogger(UserCRUDFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }//GEN-LAST:event_btnSaveActionPerformed

    private void cmbStateActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cmbStateActionPerformed
    {//GEN-HEADEREND:event_cmbStateActionPerformed
        // TODO add your handling code here:
         int index= cmbState.getSelectedIndex();
        cmbCity.removeAllItems();
        for(String city: cities.get(index))
            cmbCity.addItem(city);
    }//GEN-LAST:event_cmbStateActionPerformed

    private void tfAdd1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tfAdd1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tfAdd1ActionPerformed

    
    private String generateId()
    {
        
        try {
            String id = sdf1.format(Calendar.getInstance().getTime());
            Connection con = Main.db.createConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("select seqcode.nextVal from dual");
            rs.next();
            id+=String.format("%04d",rs.getInt(1));
            con.close();
            return id;
        } catch (SQLException ex) {
            Logger.getLogger(UserCRUDFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1+"";
    }
    
    private void btnUploadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUploadActionPerformed
        try 
        {
            // TODO add your handling code here:
            int status = chooser.showOpenDialog(this);
            if(status==JFileChooser.APPROVE_OPTION)
            {    
                File file = chooser.getSelectedFile();
                Image img = ImageIO.read(file);
                imgUser = img;
                fitImageToLabel(img, lblPicture);
            }
        } 
        catch (IOException ex) 
        {
            JOptionPane.showMessageDialog(this, "Invalid Image File!!\nPlease Select a Valid One!");
        }
    }//GEN-LAST:event_btnUploadActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        // TODO add your handling code here:
        lblPicture.setIcon(null);
        imgUser = null;
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        mode = 'e';
        enableComponents(true);
        tfName.requestFocus();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        // TODO add your handling code here:
        mode = 'n';
        enableComponents(false);
        fetchData();
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        try {
            Connection con = Main.db.createConnection();
            con.setAutoCommit(false);
            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            if(curID!=-1)
            {
                ResultSet rs = stmt.executeQuery("Select name from MyUsers where id = "+curID);
                if(rs.next())
                {
                    rs.deleteRow();
                    stmt.executeUpdate("delete from UserHobbies where userid="+curID);
                    if(lst.getSelectedIndex()==emailModel.size()-1)
                    {
                        emailModel.remove(lst.getSelectedIndex());
                        idModel.remove(lst.getSelectedIndex());
                        if(emailModel.size()>0)
                        {
                            lst.setSelectedIndex(emailModel.size()-1);
                            curID = lst.getSelectedValue().getId(); //is it needed?
                        }
                        else
                            curID = -1;
                    }
                    else
                    {
                        int index = lst.getSelectedIndex();
                        emailModel.remove(lst.getSelectedIndex());
                        idModel.remove(lst.getSelectedIndex());
                        lst.setSelectedIndex(index);
                        curID = lst.getSelectedValue().getId();
                    }
                }
            }
            con.commit();
            con.setAutoCommit(true);
            con.close();
            enableComponents(false);
            fetchData();
        } catch (SQLException ex) {
            Logger.getLogger(UserCRUDFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
    }//GEN-LAST:event_formWindowClosing
        
    private class NumericDocumentFilter extends DocumentFilter
   {

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException 
        {
            try
            {
                if(!text.isEmpty())
                    Long.parseLong(text);
                super.replace(fb, offset, length, text, attrs); //To change body of generated methods, choose Tools | Templates.
            }
            catch(Exception e)
            {
                
            }
        }
   }
    
    private void tfPhnKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tfPhnKeyTyped
        // TODO add your handling code here:
//        char ch = evt.getKeyChar();
        
       
        
        
//        doc.addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                System.out.println("insert");
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                System.out.println("REMOVE");
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                System.out.println("changed");
//            }
//        });
//        if(!Character.isDigit(ch))
//            evt.consume(); //dont let it type or show.
    }//GEN-LAST:event_tfPhnKeyTyped

    private void radEmailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radEmailActionPerformed
        // TODO add your handling code here:
        lst.setModel(emailModel);
        lst.setSelectedIndex(curID);
    }//GEN-LAST:event_radEmailActionPerformed

    private void radCodeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radCodeActionPerformed
        // TODO add your handling code here:
        lst.setModel(idModel);
        lst.setSelectedIndex(curID);
    }//GEN-LAST:event_radCodeActionPerformed

    private void lstValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstValueChanged
        // TODO add your handling code here:
            fetchData();
    }//GEN-LAST:event_lstValueChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UserCRUDFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserCRUDFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserCRUDFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserCRUDFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new UserCRUDFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnFirst;
    private javax.swing.JButton btnLast;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnNext;
    private javax.swing.JButton btnPrev;
    private javax.swing.JButton btnRemove;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JButton btnUpload;
    private javax.swing.JCheckBox chkCar;
    private javax.swing.JCheckBox chkHome;
    private javax.swing.JComboBox<String> cmbCity;
    private javax.swing.JComboBox<String> cmbState;
    private javax.swing.ButtonGroup grpGender;
    private javax.swing.ButtonGroup grpLst;
    private javax.swing.ButtonGroup grpMarital;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblPicture;
    private javax.swing.JList<Item> lst;
    private javax.swing.JList<Item> lstHobbies;
    private javax.swing.JRadioButton radBachelor;
    private javax.swing.JRadioButton radCode;
    private javax.swing.JRadioButton radEmail;
    private javax.swing.JRadioButton radFemale;
    private javax.swing.JRadioButton radMale;
    private javax.swing.JRadioButton radMarried;
    private javax.swing.JRadioButton radOthers;
    private javax.swing.JRadioButton radSeperated;
    private javax.swing.JTextArea taRemarks;
    private javax.swing.JTextField tfAdd1;
    private javax.swing.JTextField tfAdd2;
    private javax.swing.JTextField tfAdd3;
    private javax.swing.JTextField tfDob;
    private javax.swing.JTextField tfEmail;
    private javax.swing.JTextField tfName;
    private javax.swing.JTextField tfPhn;
    private javax.swing.JTextField tfUserId;
    // End of variables declaration//GEN-END:variables
}
