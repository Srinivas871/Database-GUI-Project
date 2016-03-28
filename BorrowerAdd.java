import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class BorrowerAdd implements ActionListener {

	JLabel l1,l2,l3,l4,l5;
	JTextField tf1,tf2,tf3,tf4,tf5;
	JButton submit;
	JTextField mand_instr;
	JFrame f;
	Connection con=null;
	
	/*public static void main(String[] args)
	{
		BorrowerAdd ba=new BorrowerAdd();
		ba.add();
	}*/
	
	public void add()
	{
		JPanel p_west=new JPanel();
		p_west.setLayout(new BoxLayout(p_west,BoxLayout.Y_AXIS));
		JPanel p_center=new JPanel();
		p_center.setLayout(new BoxLayout(p_center,BoxLayout.Y_AXIS));
		JPanel p_south=new JPanel();
		p_south.setLayout(new BoxLayout(p_south,BoxLayout.PAGE_AXIS));
		
		l1 =new JLabel("SSN*            : ");
		l1.setSize(20,30);
		l2 =new JLabel("First Name*: ");
		l2.setSize(20,30);
		l3 =new JLabel("Last Name*: ");
		l4 =new JLabel("Address*    : ");
		l5 =new JLabel("Phone         : ");
		
		JPanel p1 = new JPanel();
		p1.setLayout(new BoxLayout(p1,BoxLayout.X_AXIS));
		
		JPanel p2 = new JPanel();
		p2.setLayout(new BoxLayout(p2,BoxLayout.X_AXIS));
		
		JPanel p3 = new JPanel();
		p3.setLayout(new BoxLayout(p3,BoxLayout.X_AXIS));
		
		JPanel p4 = new JPanel();
		p4.setLayout(new BoxLayout(p4,BoxLayout.X_AXIS));
		
		JPanel p5 = new JPanel();
		p5.setLayout(new BoxLayout(p5,BoxLayout.X_AXIS));
		
		/*p_west.add(l1);
		p_west.add(l2);
		p_west.add(l3);
		p_west.add(l4);
		p_west.add(l5); */
		
		tf1 =new JTextField();
		tf2 =new JTextField();
		tf3 =new JTextField();
		tf4 =new JTextField();
		tf5 =new JTextField();
		
		p1.add(l1);
		p1.add(tf1);
		
		p2.add(l2);
		p2.add(tf2);
		
		p3.add(l3);
		p3.add(tf3);
		
		p4.add(l4);
		p4.add(tf4);
		
		p5.add(l5);
		p5.add(tf5);
		
		/*p_center.add(tf1);
		p_center.add(tf2);
		p_center.add(tf3);
		p_center.add(tf4);
		p_center.add(tf5); */
		
		p_center.add(p1);
		p_center.add(p2);
		p_center.add(p3);
		p_center.add(p4);
		p_center.add(p5);
		
		submit=new JButton("Submit");
		submit.setAlignmentX( Component.CENTER_ALIGNMENT); 
		submit.addActionListener(this);
		
		mand_instr=new JTextField("* -- is a mandatory field");
		
		p_south.add(submit);
		p_south.add(mand_instr);
		
		f=new JFrame();
		f.getContentPane().add(BorderLayout.CENTER,p_center);
		f.getContentPane().add(BorderLayout.SOUTH,p_south);
		f.setSize(300,300);
		f.setVisible(true);
		//f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if(tf1.getText().equals("")||tf2.getText().equals("")||tf3.getText().equals("")||tf4.getText().equals(""))
		{
			mand_instr.setText("Need to enter values for all mandatory fields as suffixed by *");
			mand_instr.setBackground(Color.RED);
		
		}
		else
		{
			JFrame created_info=null;
			
			boolean add=addToDB();
			
			if(add==true)
			JOptionPane.showMessageDialog(created_info,
				    "Borrower created ");
			
			tf1.setText(null);
			tf2.setText(null);
			tf3.setText(null);
			tf4.setText(null);
			tf5.setText(null);
			//f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		}
	}
	
	
	private boolean addToDB()
	{		
		boolean update_done=false;
		
		PreparedStatement max_id_stmt;
		ResultSet result;
		String max_id="",next_id;
		
		try{
			con= DriverManager.getConnection("jdbc:mysql://localhost:3306/library?autoReconnect=true&useSSL=false","root","Srini@871");
			max_id_stmt= con.prepareStatement("select max(Card_no) from borrower;");
			result= max_id_stmt.executeQuery();
			
			ResultSetMetaData rsmd = result.getMetaData();
			int count= rsmd.getColumnCount();
			//System.out.println(count);
			
			//getting column names
			for (int i = 1; i <= count; i++)
			{
			   System.out.print(rsmd.getColumnLabel(i) + " ");  
			}
			
			while(result.next())
			max_id=result.getString(1);
			
			int max_id_no=Integer.parseInt(max_id.substring(2));
			next_id=max_id.substring(0,2);
			Integer next_id_no=max_id_no+1;
			for(int i=next_id_no.toString().length();i<max_id.length()-2;i++)
			{
				next_id=next_id+"0";
			}
			
			next_id=next_id+next_id_no.toString();
			
			System.out.println(next_id);
			
		/*	System.out.println("insert into borrower (Card_no,Ssn,Fname,Lname,Address) "
					+ "values("+"'"+next_id+"'"+","+"'"+tf1.getText().substring(0,3)+"-"+tf1.getText().substring(3,5)+
					"-"+tf1.getText().substring(5)+"'"+
					","+ "'"+tf2.getText()+"'"+ "," +"'"+ tf3.getText()+"'"+ "," +"'"+ tf4.getText()+"'"+");"); */
			
		}catch(Exception e)
		{
			e.printStackTrace();
			update_done=false;
			return false;
		}
		
		
		
		try
		{
			PreparedStatement chk_ssn;
			//check for same SSN
		String ssn_chk_stmt="select count(*) from borrower where ssn='"+tf1.getText().substring(0,3)+"-"+tf1.getText().substring(3,5)+
				"-"+tf1.getText().substring(5)+"'";
			
		chk_ssn=con.prepareStatement(ssn_chk_stmt);
		result=chk_ssn.executeQuery();
		while(result.next())
		{
			if(!result.getString(1).equals("0"))
			{
				JFrame created_info=null;
				JOptionPane.showMessageDialog(created_info,"User with the given SSN already exists");
				return false;
			}
		}
		
		String update_stmt="insert into borrower (Card_no,Ssn,Fname,Lname,Address,Phone) "
					+ "values("+"'"+next_id+"'"+","+"'"+tf1.getText().substring(0,3)+"-"+tf1.getText().substring(3,5)+
					"-"+tf1.getText().substring(5)+"'"+
					","+ "'"+tf2.getText()+"'"+ "," +"'"+ tf3.getText()+"'"+ "," +"'"+ tf4.getText()+"'"+","+"'"+tf5.getText()+"'"+");";
		
		System.out.println(update_stmt);
		
		PreparedStatement insert_statement = con.prepareStatement(update_stmt);
		
		insert_statement.executeUpdate();
		
		update_done= true;
		
		}catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception in update");
			update_done=false;
		}
		
		return update_done;
	}
}


