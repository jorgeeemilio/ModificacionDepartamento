package es.studium.Tema7;

import java.awt.Button;
import java.awt.Choice;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ModificacionDepartamento implements WindowListener, ActionListener		
{
	// Atributos, Componentes
	Frame ventana = new Frame("Editar Departamento");
	Label lblTitular = new Label("Indicar el departamento a editar:");
	Choice choDepartamentos = new Choice();
	Button btnEditar = new Button("Editar");
	
	Dialog dlgEdicion = new Dialog(ventana, "Edición", true);
	Label lblNombre = new Label("Nombre departamento:");
	TextField txtNombre = new TextField(30);
	Label lblLocalidad = new Label("Localidad departamento:");
	TextField txtLocalidad = new TextField(30);
	Button btnModificar = new Button("Modificar");
	Button btnCancelar = new Button("Cancelar");
	int idDepartamento;
	
	Dialog dlgFeedback = new Dialog(ventana, "Feedback", true);
	Label lblMensaje = new Label("XXXXXXXXXXXXXXXXX");

	// Relativos a la BD
	String driver = "com.mysql.cj.jdbc.Driver";
	String url = "jdbc:mysql://localhost:3306/gestion";
	String login = "root";
	String password = "Studium2021;";
	String sentencia = "";
	Connection connection = null;
	Statement statement = null;
	ResultSet resultset = null;

	// Constructor
	public ModificacionDepartamento()
	{
		// Montar el GUI
		ventana.setLayout(new FlowLayout());

		// Listener
		ventana.addWindowListener(this);
		btnEditar.addActionListener(this);
		dlgEdicion.addWindowListener(this);
		btnModificar.addActionListener(this);
		btnCancelar.addActionListener(this);
		dlgFeedback.addWindowListener(this);

		// Ventana, botones, ...
		ventana.add(lblTitular);
		conectar();
		rellenarChoice();
		desconectar();
		ventana.add(choDepartamentos);
		ventana.add(btnEditar);

		ventana.setSize(300,200);
		ventana.setResizable(false);
		ventana.setLocationRelativeTo(null);
		ventana.setVisible(true);
	}

	public static void main(String[] args)
	{
		new ModificacionDepartamento();
	}
	@Override
	public void windowOpened(WindowEvent e){}
	@Override
	public void windowClosing(WindowEvent e)
	{
		if(dlgEdicion.isActive())
		{
			dlgEdicion.setVisible(false);
		}
		else if(dlgFeedback.isActive())
		{
			dlgFeedback.setVisible(false);
		}
		else
		{
			System.exit(0);
		}
	}
	@Override
	public void windowClosed(WindowEvent e){}
	@Override
	public void windowIconified(WindowEvent e){}
	@Override
	public void windowDeiconified(WindowEvent e){}
	@Override
	public void windowActivated(WindowEvent e){}
	@Override
	public void windowDeactivated(WindowEvent e){}
	public void conectar()
	{
		try
		{
			// Cargar los controladores para el acceso a la BD
			Class.forName(driver);
			// Establecer la conexión con la BD Empresa
			connection = DriverManager.getConnection(url, login, password);
		}
		catch (SQLException sqle){}
		catch (ClassNotFoundException cnfe){}
	}
	public void rellenarChoice()
	{
		choDepartamentos.removeAll();
		choDepartamentos.add("Seleccionar un departamento para editar...");
		// Crear una sentencia
		try
		{
			statement = connection.createStatement();
			sentencia = "SELECT * FROM departamentos";
			resultset = statement.executeQuery(sentencia);
			while(resultset.next())
			{
				choDepartamentos.add(resultset.getInt("idDepartamento")+
						"-"+ resultset.getString(2)+
						"-"+ resultset.getString(3));
			}
		} 
		catch (SQLException e){}	
	}
	public void desconectar()
	{
		try
		{
			if(connection!=null)
			{
				connection.close();
			}
		}
		catch (SQLException e){}
	}
	
	public void actionPerformed(ActionEvent evento)
	{
		if(evento.getSource().equals(btnEditar))
		{
			// Montar el Diálogo
			dlgEdicion.setLayout(new FlowLayout());
			
			dlgEdicion.add(lblNombre);
			String[] seleccionado = choDepartamentos.getSelectedItem().split("-");
			idDepartamento = Integer.parseInt(seleccionado[0]);
			txtNombre.setText(seleccionado[1]);
			dlgEdicion.add(txtNombre);
			dlgEdicion.add(lblLocalidad);
			txtLocalidad.setText(seleccionado[2]);
			dlgEdicion.add(txtLocalidad);
			dlgEdicion.add(btnModificar);
			dlgEdicion.add(btnCancelar);
			
			dlgEdicion.setSize(300,200);
			dlgEdicion.setResizable(false);
			dlgEdicion.setLocationRelativeTo(null);
			dlgEdicion.setVisible(true);
		}
		else if(evento.getSource().equals(btnCancelar))
		{
			dlgEdicion.setVisible(false);
		}
		else if(evento.getSource().equals(btnModificar))
		{
			// Coger los datos
			String nombreNuevo = txtNombre.getText();
			String localidadNueva = txtLocalidad.getText();
			// Montar la sentencia
			sentencia = "UPDATE departamentos SET nombreDepartamento = '"+
			nombreNuevo + "', localidadDepartamento ='" +
			localidadNueva + "' WHERE idDepartamento = " + idDepartamento;
			conectar();
			try
			{
				statement = connection.createStatement();
				statement.executeUpdate(sentencia);
				lblMensaje.setText("Modificación correcta");
				mostrarDialogoFeedback();
				rellenarChoice();
				dlgEdicion.setVisible(false);
			} 
			catch (SQLException e)
			{
				lblMensaje.setText("Modificación errónea");
				mostrarDialogoFeedback();
			}
			desconectar();
		}
	}

	private void mostrarDialogoFeedback()
	{
		dlgFeedback.setLayout(new FlowLayout());
		dlgFeedback.add(lblMensaje);
		dlgFeedback.setSize(150,100);
		dlgFeedback.setResizable(false);
		dlgFeedback.setLocationRelativeTo(null);
		dlgFeedback.setVisible(true);	
	}
}