//
// Klasse enthält den Zusammenhang zwischen 2 Hashtags
//
public class erscheintzsmmit {
	public String name1 = null;	// Name des 1. Hashtags
	public String name2 = null;	// Name des 2. Hashtags
	public int count = 0;		// Wie oft kommen beide in zusammen in einem Tweet vor
	public erscheintzsmmit(String name1,String name2)
	{
		this.name1 = name1;
		this.name2 = name2;
		count = 1;
	}
}
