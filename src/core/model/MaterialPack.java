package core.model;

import java.util.List;

public class MaterialPack {
	
	private List<Material> materials;
	
	public MaterialPack(List<Material> materials) {
		this.materials = materials;
	}
	
	public void addMaterial(Material material) {
		materials.add(material);
	}
	
	public boolean removeMaterial(Material material) {
		return materials.remove(material);
	}
	
	public int getMaterialCount() {
		return materials.size();
	}

	public List<Material> getMaterials() {
		return materials;
	}
}
