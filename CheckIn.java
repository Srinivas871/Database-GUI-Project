import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


public class CheckIn {

	JLabel book_id_label,Card_no_Label,brwr_fname_label, brwr_lname_label,mand_instr;
	JTextField book_id_tf,Card_no_tf,brwr_fname_tf,brwr_lname_tf;
	JFrame EntryScreen,SearchResScreen;
	JButton search,checkInBt;
	JTable CheckInResTable;
	Connection con=null;
	String[][] table_search_res=new String[150][];
	String search_res;
	int linecount;
	ListSelectionModel model;
	int selectedRow=-1;
	
	/*public static void main(String[] args) {
		// TODO Auto-generated method stub
		CheckIn ci=new CheckIn();
		ci.EnterData();
	}*/

	public void EnterData()
	{
		book_id_label=new JLabel("Book Id                          : "); //isbn no 
		Card_no_Label=new JLabel("Card No                         : "); //card no
		brwr_fname_label = new JLabel("Borrower First Name : ");
		brwr_lname_label = new JLabel("Borrower Last name : ");
		
		book_id_tf=new JTextField();
		Card_no_tf=new JTextField();
		brwr_fname_tf=new JTextField();
		brwr_lname_tf=new JTextField();
		
		JPanel p1=new JPanel();
		JPanel p2=new JPanel();
		JPanel p3=new JPanel();
		JPanel p4=new JPanel();
		
		p1.setLayout(new BoxLayout(p1,BoxLayout.X_AXIS));
		p2.setLayout(new BoxLayout(p2,BoxLayout.X_AXIS));
		p3.setLayout(new BoxLayout(p3,BoxLayout.X_AXIS));
		p4.setLayout(new BoxLayout(p4,BoxLayout.X_AXIS));
		
		p1.add(book_id_label); p1.add(book_id_tf);
		p2.add(Card_no_Label); p2.add(Card_no_tf);
		p3.add(brwr_fname_label); p3.add(brwr_fname_tf);
		p4.add(brwr_lname_label);p4.add(brwr_lname_tf);
		
		JPanel p_center=new JPanel();
		p_center.setLayout(new BoxLayout(p_center,BoxLayout.Y_AXIS));
		
		p_center.add(p1);p_center.add(p2);p_center.add(p3);p_center.add(p4);
		
		
		mand_instr=new JLabel("Enter any of the values");
		mand_instr.setAlignmentX(Component.RIGHT_ALIGNMENT);
		search=new JButton("Search");
		search.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		JPanel p_south=new JPanel();
		p_south.setLayout(new BoxLayout(p_south,BoxLayout.Y_AXIS));
		
		p_south.add(search);
		p_south.add(mand_instr);
		
		EntryScreen=new JFrame();
		EntryScreen.getContentPane().add(BorderLayout.CENTER,p_center);
		EntryScreen.getContentPane().add(BorderLayout.SOUTH,p_south);
		EntryScreen.setSize(300,300);
		EntryScreen.setVisible(true);
		
		search.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0) {
				//Calling CheckInSearch
				CheckInSearch();
			}
		});
		
		
		//EntryScreen.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	
	private void CheckInSearch()
	{
		PreparedStatement book_id_loans,card_no_loans,borrower_name_stmt,main_stmt;
		ResultSet result;
		ArrayList<String> Loan_ids =new ArrayList<>();
		linecount=0;
		Loan_ids.clear();
		
		try {
			
		con= DriverManager.getConnection("jdbc:mysql://localhost:3306/library?autoReconnect=true&useSSL=false","root","Srini@871");
		
		if((!book_id_tf.getText().trim().equals(""))&&Card_no_tf.getText().trim().equals("")&&brwr_fname_tf.getText().trim().equals("")&&brwr_lname_tf.getText().trim().equals(""))
		{
			
			book_id_loans = con.prepareStatement("select loan_id from book_loans where isbn='"+book_id_tf.getText().trim()+"';");
			result=book_id_loans.executeQuery();
			
			while(result.next())
				Loan_ids.add(result.getString(1));
		}	
		
		else if(book_id_tf.getText().trim().equals("")&&!Card_no_tf.getText().trim().equals("")&&brwr_fname_tf.getText().trim().equals("")&&brwr_lname_tf.getText().trim().equals(""))
		{
			card_no_loans=con.prepareStatement("select loan_id from book_loans where Card_no='"+Card_no_tf.getText().trim()+"';");
			result=card_no_loans.executeQuery();
			
			result=card_no_loans.executeQuery();
			
			while(result.next())
				Loan_ids.add(result.getString(1));
		}
		
		else if(book_id_tf.getText().trim().equals("")&&Card_no_tf.getText().trim().equals("")&&(!brwr_fname_tf.getText().trim().equals("")||!brwr_lname_tf.getText().trim().equals("")))
		{
			ArrayList<String> CardNos= new ArrayList<String>();
			
			if(!brwr_fname_tf.getText().trim().equals("")&&!brwr_lname_tf.getText().trim().equals(""))
			{
			borrower_name_stmt=con.prepareStatement("select Card_no from borrower "
					+ "where fname='"+brwr_fname_tf.getText().trim()+"' and "
					+ "lname='"+brwr_lname_tf.getText().trim()+"';");
			
			result=borrower_name_stmt.executeQuery();
			}
			
			else if(!brwr_fname_tf.getText().trim().equals(""))
			{
				borrower_name_stmt=con.prepareStatement("select Card_no from borrower "
						+ "where fname='"+brwr_fname_tf.getText().trim()+"';");	
				result=borrower_name_stmt.executeQuery();
			}
			else
			{
				borrower_name_stmt=con.prepareStatement("select Card_no from borrower "
						+ "where lname='"+brwr_lname_tf.getText().trim()+"';");
				result=borrower_name_stmt.executeQuery();
			}
			
			while(result.next())
				CardNos.add(result.getString(1));
			
			for(String card_one:CardNos)
			{
				card_no_loans=con.prepareStatement("select loan_id from book_loans where Card_no='"+card_one+"';");
				result=card_no_loans.executeQuery();
				
				result=card_no_loans.executeQuery();
				
				while(result.next())
					Loan_ids.add(result.getString(1));
			}
			
		}
		
		for(String loan_id_one: Loan_ids)
		{
			
		main_stmt=con.prepareStatement("select Loan_id,'^',Isbn,'^',Branch_id,'^',"
				+ "Card_no,'^',Date_out,'^',Due_date,'^',Date_in "
				+ "from book_loans where loan_id="+loan_id_one+";");
		
		result=main_stmt.executeQuery();
		
		ResultSetMetaData rsmd = result.getMetaData();
		int count= rsmd.getColumnCount();
		//System.out.println(count);
		
		//getting column names
		for (int i = 1; i <= count; i++)
		{
		  // System.out.print(rsmd.getColumnLabel(i) + " ");  
		   search_res=search_res+rsmd.getColumnLabel(i)+"\t";
		}
		
		while(result.next())
		{
		    //System.out.println(result.getString(1)+" "+result.getString(2));
			//get 1st and 2nd columns
			String sql_row="";
		   for(int i=1; i<=count; i++)
			{
				//System.out.print(result.getString(i)+" ");
				search_res=search_res+result.getString(i);
				sql_row=sql_row+result.getString(i);
			}
		
		   table_search_res[linecount]=new String[7];
		   table_search_res[linecount]=sql_row.split("\\^",7);
		   search_res=search_res+"\n";
			linecount++;
		}
		
		if(linecount>=40) // break after 10 rows
			break;
		
	 }//for loop ends
		//calling formatting table
		centerFormat();
		
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(Exception e)
			{
				
			}		
	}
	
	public void centerFormat()
	{
		CheckInResTable=null;
		String[] columnNames={"Loan_id", "ISBN", "Branch_id", "Card_no" , "Date_out","Due_date","Date_in"};
		String[][] table_res1=new String[linecount][];
		
		System.out.println("linecount "+linecount);
		
		for(int i=0;i<linecount;i++)
			table_res1[i]=table_search_res[i];
		
		Object [] [] data = table_res1;
		/*Object [] [] data = {
				{"0001360469","Dressing (Collins Baby &amp; Toddler: Babe Board Books)","Roger E. Allen,Stephen D. Allen,Bob Davis,Shirley",
					"4","Grauwyler Park","3","1"},
				{"0001360469","Dressing (Collins Baby &amp; Toddler: Babe Board Books)","Roger E. Allen,Stephen D. Allen,Bob Davis,Shirley",
					"4","Grauwyler Park","3","1"},
				{"0001360469","Dressing (Collins Baby &amp; Toddler: Babe Board Books)","Roger E. Allen,Stephen D. Allen,Bob Davis,Shirley",
					"4","Grauwyler Park","3","1"},
		};*/
	
		CheckInResTable=new JTable(data,columnNames);
		CheckInResTable.setPreferredScrollableViewportSize(new Dimension(500,500));
		CheckInResTable.setFillsViewportHeight(true);
		
		model=CheckInResTable.getSelectionModel();	
		
		JScrollPane scpane= new JScrollPane(CheckInResTable);
		JPanel p_center_table= new JPanel();
		JPanel p_south= new JPanel();
		//p_center_table.setLayout(new BoxLayout(p_center_table,BoxLayout.Y_AXIS));
		//p_center_table.removeAll();
		p_center_table.removeAll();
		p_center_table.add(scpane);
		
		checkInBt=new JButton("Check-IN");
		checkInBt.setAlignmentX(Component.RIGHT_ALIGNMENT);
		p_south.add(checkInBt);
		
		SearchResScreen=new JFrame();
		SearchResScreen.setSize(600,600);
		SearchResScreen.getContentPane().add(BorderLayout.CENTER,p_center_table);
		SearchResScreen.getContentPane().add(BorderLayout.SOUTH,p_south);
		
		EntryScreen.dispose();
		
		SearchResScreen.setVisible(true);
		
		checkInBt.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent arg0) 
			{
				if(selectedRow==-1)
				{
					JFrame created_info=null;
					JOptionPane.showMessageDialog(created_info,"Select a row to check out");
				}
				else
				{
					CheckinSelectedBook(table_search_res[selectedRow][0]);
				}
				selectedRow=-1;
				SearchResScreen.dispose();
				book_id_tf=null;
				Card_no_tf=null;
				brwr_fname_tf=null;
				brwr_lname_tf=null;
				EnterData();
			}
			
		});
		
		 model.addListSelectionListener( new ListSelectionListener(){
 			@Override
 			public void valueChanged(ListSelectionEvent e) {
 				// TODO Auto-generated method stub
 				if(!model.isSelectionEmpty())
 				{
 				selectedRow=model.getMinSelectionIndex();
 				System.out.println("Row Selected: "+selectedRow);
 				
 				/*if(table_res[selectedRow][6].equals("0"))
 				{
 					JFrame created_info=null;
 					JOptionPane.showMessageDialog(created_info,"No Available copies for this branch");
 				}*/
 				
 				}    }
			});
	
	}
	
	private void CheckinSelectedBook(String Sel_Loan_id)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		   //get current date time with Date()
		Date date = new Date();
		
		String Book_loan_update_stmt="update book_loans set Date_in ='"+dateFormat.format(date)+"' where loan_id="+Sel_Loan_id+";";
		
		try
		{
		PreparedStatement loan_update=con.prepareStatement(Book_loan_update_stmt);
		loan_update.executeUpdate();
		}
		catch(SQLException se)
		{
			se.printStackTrace();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	
}
	
