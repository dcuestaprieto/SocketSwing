import java.util.ArrayList;

import model.Entrada;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;

public class DataSource implements JRDataSource{
	
	private int index;
	private ArrayList<Entrada> lista;
	
	public DataSource(ArrayList<Entrada> lista) {
		this.lista = lista;
		index = -1;
	}

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		Object object = new Object();
		
		if(field.getName().equals("TierEntrada")) {
			object = lista.get(index).getTierEntrada();
		}else if (field.getName().equals("CantidadEntradas")) {
			object = lista.get(index).getCantidadEntradas();
		}
		
		return object;
	}

	@Override
	public boolean next() throws JRException {
		index++;
		return index<lista.size();
	}

}
