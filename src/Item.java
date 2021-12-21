class Item {
	private String name;
	private int weight;
	private int cost;


	//constructor for item
	
	public Item (String name, int weight, int cost) {
		this.name = name;
		this.weight = weight;
		this.cost = cost;
	}

	//getters

	public String getName() {
		return name;
	}

	public int getWeight() {
		return weight;
	}

	public int getCost() {
		return cost;
	}
	
}