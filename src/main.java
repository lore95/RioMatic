import java.util.ArrayList;

import it.unibz.mngeng.java.DBUtilty.Areas;
import it.unibz.mngeng.java.Exceptions.RMException;


public class main {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws RMException
	{
		Areas a = new Areas();
		@SuppressWarnings("unchecked")
		ArrayList<Areas> aList = (ArrayList<Areas>) a.populateCollectionOnCondition("", Areas.class);
		for(Areas item : aList)
		{
			System.out.print("field " + item.getFieldId() + " - area id " + item.getId() + " - moist " + item.getMoistureMin() +
					" - time " + item.getWateringTime());
		}
	}

}
