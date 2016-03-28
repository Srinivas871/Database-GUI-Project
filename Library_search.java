import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import java.awt.*;
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

public class Library_search implements ActionListener {
	//initialization section
	JTextArea jta;
	JTextField jtfn1,jtfn2,jtfn3;
	JLabel ln1,ln2,ln3,lntop;
	JLabel lc1,lc2,lc3,lc4,lc5,lc6,lc7,lcbefore;
	JButton Isbn_checkout;
	JButton b1,b2,b3,b4,b5;
	String search_res="";
	JFrame f;
	JPanel p_center;
	String[][] table_res=new String[150][];
	static int linecount=0;
	JScrollPane scpane;
	Connection con=null;
	JTable table;
	ListSelectionModel model;
	int selectedRow=-1;
	
	static int i;
	public static void main(String[] args)
	{
		Library_search vd=new Library_search();
		vd.go();
	}

	public void go(){
		
		//initialization section
		f=new JFrame("Library Database");
		b1=new JButton("Search");
		b2=new JButton("Check-out");
		b3=new JButton("Check-in");
		b4=new JButton("Fines");
		b5=new JButton("New Borrower");
		ln1=new JLabel("Title:      ");
		ln2=new JLabel("Author: ");
		ln3=new JLabel("ISBN:     ");
		lntop=new JLabel("Search using Title or Author or ISBN.");
		jta=new JTextArea("Demonstartion of Library Retrieval");
		b1.addActionListener(this);
		JPanel p_east=new JPanel();
		JPanel p_north=new JPanel();
		p_center=new JPanel();
		JPanel pn1,pn2,pn3;
		JPanel pc1,pc2,pc3;
		jtfn1= new JTextField();
		jtfn2= new JTextField();
		jtfn3= new JTextField();
		
		
		b1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
	            System.out.println("Button1 is pressed");
	            if(!jtfn1.getText().trim().equals("")|| !jtfn2.getText().trim().equals("")|| !jtfn3.getText().trim().equals(""))
	            {
	            
	            	jta.setText("Getting Results...");
	            	search_res="";
	            	search();
	            	//System.out.println(search_res);
	            	jta.setText("");
	            	jta.setText(search_res);
	            	centerFormat();
	            	f.getContentPane().add(BorderLayout.CENTER,p_center);
	            	SwingUtilities.updateComponentTreeUI(f);
	            	f.setVisible(true);
	            }
	            
	            model.addListSelectionListener( new ListSelectionListener(){
	    			@Override
	    			public void valueChanged(ListSelectionEvent e) {
	    				// TODO Auto-generated method stub
	    				if(!model.isSelectionEmpty())
	    				{
	    				selectedRow=model.getMinSelectionIndex();
	    				System.out.println("Row Selected: "+selectedRow);
	    				System.out.println("Available Copies: "+table_res[selectedRow][6]);
	    				
	    				/*if(table_res[selectedRow][6].equals("0"))
	    				{
	    					JFrame created_info=null;
	    					JOptionPane.showMessageDialog(created_info,"No Available copies for this branch");
	    				}*/
	    				
	    				}
	            
	        }
		});
			}
		});
		
		
		//Check-Out functionality
		b2.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				if(selectedRow==-1)
				{
					JFrame created_info=null;
					JOptionPane.showMessageDialog(created_info,"Select a row to check out");
				}
				else
				{
					if(table_res[selectedRow][6].equals("0"))
    				{
    					JFrame created_info=null;
    					JOptionPane.showMessageDialog(created_info,"No Available copies for this branch");
    				}
					else
					{
						System.out.println("Button2 Check-out is pressed");
			            CheckOut co=new CheckOut();
			    		co.checkOutBook(table_res[selectedRow][0],table_res[selectedRow][1],table_res[selectedRow][3],table_res[selectedRow][6]);
					}
				}
				//do again search
				centerFormat();
				selectedRow=-1;
				SwingUtilities.updateComponentTreeUI(f);
				
				   model.addListSelectionListener( new ListSelectionListener(){
		    			@Override
		    			public void valueChanged(ListSelectionEvent e) {
		    				// TODO Auto-generated method stub
		    				if(!model.isSelectionEmpty())
		    				{
		    				selectedRow=model.getMinSelectionIndex();
		    				System.out.println("Row Selected: "+selectedRow);
		    				System.out.println("Available Copies: "+table_res[selectedRow][6]);
		    				
		    				}
		            
		        }
			});
			}
			
		});
		
		
		
		
		b5.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
	            System.out.println("Button5 is pressed");
	            BorrowerAdd ba=new BorrowerAdd();
	    		ba.add();
	        }
		});
		
		b3.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
	            System.out.println("Button3 is pressed");
	            CheckIn ci=new CheckIn();
	    		ci.EnterData();
	        }
		});
		
		b4.addActionListener(new ActionListener(){

			public void actionPerformed(ActionEvent e) {
				System.out.println("Button4 -- Fines is pressed");
	            Fines fi=new Fines();
	            fi.updateRegularFines();
	    		fi.FinesDisplay();
			}
			
		});
		
		
		p_east.setLayout(new BoxLayout(p_east,BoxLayout.Y_AXIS));
		p_east.setBackground(Color.cyan);
		p_east.add(b1);
		p_east.add(b2);
		p_east.add(b3);
		p_east.add(b4);
		p_east.add(b5);
		
		//north side layout of the frame, covers all criteria
		
		p_north.setLayout(new BoxLayout(p_north,BoxLayout.Y_AXIS));
		
		pn1=new JPanel();
		pn1.setLayout(new BoxLayout(pn1,BoxLayout.X_AXIS));
		pn2=new JPanel();
		pn2.setLayout(new BoxLayout(pn2,BoxLayout.X_AXIS));
		pn3=new JPanel();
		pn3.setLayout(new BoxLayout(pn3,BoxLayout.X_AXIS));
		
		pn1.add(ln1);
		pn1.add(jtfn1);
		
		pn2.add(ln2);
		pn2.add(jtfn2);
		
		pn3.add(ln3);
		pn3.add(jtfn3);
		
		p_north.add(lntop);
		p_north.add(pn1);
		p_north.add(pn2);
		p_north.add(pn3);
		
		
		p_center.setLayout(new BoxLayout(p_center,BoxLayout.Y_AXIS));
		//p_center.add(jta);
		
		//frame locations
		f.getContentPane().add(BorderLayout.NORTH,p_north);
		f.getContentPane().add(BorderLayout.EAST,p_east);
		f.setSize(1200,700);
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
	}
	
	public void search()
	{
		ArrayList<String> isbns=new ArrayList<String>();
		int count;
		linecount=0;
		isbns.clear();
		
		try
		{
		Class.forName("com.mysql.jdbc.Driver");
		
		con= DriverManager.getConnection("jdbc:mysql://localhost:3306/library?autoReconnect=true&useSSL=false","root","Srini@871");
		//connect to the database,username,password
		
		/* PreparedStatement statement1 = con.prepareStatement("update actor set first_name='Srinivas' where actor_id=1");
		
		ResultSet result1= statement1.executeQuery(); */
		
		String search_stmt="";
		PreparedStatement statement;
		ResultSet result;
		String isbn_authors="nothing";
		
		//one not null
		if(!jtfn1.getText().equals("") && jtfn2.getText().equals("") && jtfn3.getText().equals(""))
		{
			String authors_isbn_stmt="select isbn from book where title like '%"+jtfn1.getText()+"%'";
			
			statement = con.prepareStatement(authors_isbn_stmt);
			result= statement.executeQuery();
			
			int isbn_count=1;
			while(result.next())
			{
			isbns.add(result.getString(1));
			isbn_count++;
			}
			System.out.println(isbn_count);
			
			//for(String isbn_print:isbns)
			//System.out.println(isbn_print);
		}
		
		// one and three not null
		else if(!jtfn1.getText().equals("") && jtfn2.getText().equals("") && !jtfn3.getText().equals(""))
		{
			String authors_isbn_stmt="select isbn from book where title like '%"+jtfn1.getText()+"%' and isbn like '%"+jtfn3.getText().trim()+"%';";
			
			statement = con.prepareStatement(authors_isbn_stmt);
			result= statement.executeQuery();
			
			int isbn_count=1;
			while(result.next())
			{
			isbns.add(result.getString(1));
			isbn_count++;
			}
			System.out.println(isbn_count);
			
			//for(String isbn_print:isbns)
			//System.out.println(isbn_print);
		}
		
		//two not null
		else if(jtfn1.getText().equals("") && !jtfn2.getText().equals("") && jtfn3.getText().equals(""))
		{
			String authors_id_stmt="select author_id from authors where fullname like '%"+jtfn2.getText()+"%'";
			statement = con.prepareStatement(authors_id_stmt);
			result= statement.executeQuery();
			
			ArrayList<Integer> alid = new ArrayList<>();
			while(result.next())
			{
			alid.add(Integer.parseInt(result.getString(1)));
			}
			
			for(int aid:alid)
			{
				String isbn_authors_id="select isbn from book_authors where author_id="+aid+";";
				statement = con.prepareStatement(isbn_authors_id);
				result= statement.executeQuery();
				int isbn_count=1;
				
				while(result.next())
				{
				isbns.add(result.getString(1));
				isbn_count++;
				}
				
			System.out.println(isbn_count);
			//for(String isbn_print:isbns)
			//System.out.println(isbn_print);	
			}
		
		}
		
		//one and two not null
		else if(!jtfn1.getText().equals("") && !jtfn2.getText().equals("") && jtfn3.getText().equals(""))
		{
			ArrayList<String> temp_isbns=new ArrayList<>();
			String authors_id_stmt="select author_id from authors where fullname like '%"+jtfn2.getText()+"%'";
			statement = con.prepareStatement(authors_id_stmt);
			result= statement.executeQuery();
			
			ArrayList<Integer> alid = new ArrayList<>();
			while(result.next())
			{
			alid.add(Integer.parseInt(result.getString(1)));
			}
			
			for(int aid:alid)
			{
				String isbn_authors_id="select isbn from book_authors where author_id="+aid+";";
				statement = con.prepareStatement(isbn_authors_id);
				result= statement.executeQuery();
				int isbn_count=1;
				
				while(result.next())
				{
				temp_isbns.add(result.getString(1));
				isbn_count++;
				}
				
				
			System.out.println(isbn_count);
				
			}
			//search using temp_ids
			for(String temp_id_one:temp_isbns)
			{
				String isbn_authors_id="select isbn from book where isbn='"
								+temp_id_one+"' and title like '%"+jtfn1.getText().trim()+"%';";
				statement = con.prepareStatement(isbn_authors_id);
				result= statement.executeQuery();
				int isbn_count=1;
				
				while(result.next())
				{
				isbns.add(result.getString(1));
				isbn_count++;
				}
				
				
			System.out.println(isbn_count);
				
			}
			
		
		}
		
		// three not null
		else if(jtfn1.getText().equals("") && jtfn2.getText().equals("") && !jtfn3.getText().equals(""))
		{
			isbns.add(jtfn3.getText().trim());
		}
		
		//all three not null
		else if(!jtfn1.getText().equals("") && !jtfn2.getText().equals("") && !jtfn3.getText().equals(""))
		{
			isbns.add(jtfn3.getText().trim());
		}
		
		// two and three not null
		else if(jtfn1.getText().equals("") && !jtfn2.getText().equals("") && !jtfn3.getText().equals(""))
		{
			isbns.add(jtfn3.getText().trim());
		}
		
		for(String isbn_one:isbns)
		{
		search_stmt="select bc.book_id,'^',bc.branch_id,'^',bc.No_of_copies,'^',"
				+ "b.title,'^',lb.branch_name"
				+ " from book_copies bc,book b,library_branch lb"
				+ " where bc.book_id like '%"+isbn_one+"%' "
				+ " and bc.book_id=b.isbn and lb.branch_id=bc.branch_id;"; // statement to get all except authors
		
		//statement for getting all authors
		String authors_stmt="select group_concat(a.fullname)"
				+ "from book_authors ba, authors a "
				+ "where ba.isbn='"+isbn_one+ "' and ba.author_id=a.author_id;";
		
		statement = con.prepareStatement(authors_stmt);
		result= statement.executeQuery();
		
		while(result.next())
		isbn_authors=result.getString(1);
		
		//System.out.println(isbn_authors); //printing authors
		
		statement = con.prepareStatement(search_stmt);
		
		result= statement.executeQuery();
		
		ResultSetMetaData rsmd = result.getMetaData();
		count= rsmd.getColumnCount();
		//System.out.println(count);
		
		//getting column names
		for (int i = 1; i <= count; i++)
		{
		  // System.out.print(rsmd.getColumnLabel(i) + " ");  
		   search_res=search_res+rsmd.getColumnLabel(i)+"\t";
		}
		//System.out.println();
		search_res=search_res+"\n";
		
		
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
		   sql_row=sql_row+"^"+isbn_authors; //adding authors
		   
		   String available_copies=Availability(sql_row.substring(0,10),sql_row.substring(11, 12)); //computing availability
		   int bkbrnchcopies=Integer.parseInt(sql_row.substring(13, 14));
		   sql_row=sql_row+"^"+(bkbrnchcopies-Integer.parseInt(available_copies)); //available copies
		   
		   table_res[linecount]=new String[7];
		   table_res[linecount]=sql_row.split("\\^",7);
		   
		 /*  for(String s: table_res[linecount])
		   {
			   System.out.print(s+" ");
		   } */
			
		   //System.out.println();
			search_res=search_res+"\n";
			linecount++;
		} 
		
		//System.out.println(search_res);
		
		if(linecount>=100) // break after 10 rows
		break;
		
		} // end of for for isbns
		
		}
		catch(SQLException se)
		{
			se.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		finally{
			try
			{
		con.close();
			}catch(Exception e)
			{
				e.printStackTrace();
				System.out.println("Connection improperly closed");
			}
		}
	}
	

	public void centerFormat()
	{
		table=null;
		String[] columnNames={"ISBN", "Branch ID", "Copies", "Book title" , "Branch Name","Book author(s)","Available Copies"};
		String[][] table_res1=new String[linecount][];
		
		System.out.println("linecount "+linecount);
		
		for(i=0;i<linecount;i++)
			table_res1[i]=table_res[i];
		
		Object [] [] data = table_res1;
		/*Object [] [] data = {
				{"0001360469","Dressing (Collins Baby &amp; Toddler: Babe Board Books)","Roger E. Allen,Stephen D. Allen,Bob Davis,Shirley",
					"4","Grauwyler Park","3","1"},
				{"0001360469","Dressing (Collins Baby &amp; Toddler: Babe Board Books)","Roger E. Allen,Stephen D. Allen,Bob Davis,Shirley",
					"4","Grauwyler Park","3","1"},
				{"0001360469","Dressing (Collins Baby &amp; Toddler: Babe Board Books)","Roger E. Allen,Stephen D. Allen,Bob Davis,Shirley",
					"4","Grauwyler Park","3","1"},
		};*/
	
		table=new JTable(data,columnNames);
		table.setPreferredScrollableViewportSize(new Dimension(500,500));
		table.setFillsViewportHeight(true);
		
		model=table.getSelectionModel();	
		
		JScrollPane scpane= new JScrollPane(table);
		JPanel p_center_table= new JPanel();
		//p_center_table.setLayout(new BoxLayout(p_center_table,BoxLayout.Y_AXIS));
		//p_center_table.removeAll();
		p_center.removeAll();
		p_center.add(scpane);
		//p_center.add(table.getTableHeader());
		//p_center.add(table);
		//p_center.add(p_center_table);		
	}
	
	public String Availability(String isbn_no, String bid)
	{
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		   //get current date time with Date()
		   Date date = new Date();
		   System.out.println(dateFormat.format(date));
		   
		//Show only those books which ar being returned after current date and have no Date_in
		String book_cnt_stmt="select count(*) from book_loans where isbn='"+isbn_no+"' and branch_id="+bid+
				" and (Date_in>'"+dateFormat.format(date)+"' || Date_in is NULL);";
		
		PreparedStatement statement;
		ResultSet result;
		String copies="";
		try
		{
		statement = con.prepareStatement(book_cnt_stmt);
		result=statement.executeQuery();
		
		while(result.next())
			 copies=result.getString(1);
		}
		catch(Exception e)
		{
			copies="0";
		}
		return copies;
	}
	
}