public class MapDriverST {
	private static final StatusMap sm = new StatusMap();
	private static final ReadWriteStatusMap rwSM = new ReadWriteStatusMap();	//Status map experiment using ReadWrite java locks
	
	public static void main(String args[]) {
		//Object 0 - 2D space
		Arrow pos0 = new Arrow(0L, 0L, 0L);
		Arrow vel0 = new Arrow(1L, 1L, 0L);
		Arrow acc0 = new Arrow(1L, 1L, 0L);
		Trajectory t0 = new Trajectory(pos0, vel0, acc0);
		
		//Object1 - Simple 3D space
		Arrow pos1 = new Arrow(0L, 0L, 0L);
		Arrow vel1 = new Arrow(1L, 1L, 1L);
		Arrow acc1 = new Arrow(1L, 1L, 1L);
		Trajectory t1 = new Trajectory(pos1, vel1, acc1);
		
		//Object2 - More involved 2D
		Arrow pos2 = new Arrow(5L, 2L, 3L);
		Arrow vel2 = new Arrow(2L, 1L, -1L);
		Arrow acc2 = new Arrow(-2L, 2L, 3L);
		Trajectory t2 = new Trajectory(pos2, vel2, acc2);
		
		//Object 3 - More involved 3D
		Arrow pos3 = new Arrow(-2L, 1L, 3L);
		Arrow vel3 = new Arrow(3L, 0L, 2L);
		Arrow acc3 = new Arrow(5L, -1L, 2L);
		Trajectory t3 = new Trajectory(pos3, vel3, acc3);
		
		//Insert objects into status map
		sm.insert(new String("object0"), t0);
		sm.insert(new String("object1"), t1);
		sm.insert(new String("object2"), t2);
		sm.insert(new String("object3"), t3);
		
		//Insert objects into rwStatusMap (insert makes a defensive copy so this will not interfere with other test)
		rwSM.insert(new String("object0"), t0);
		rwSM.insert(new String("object1"), t1);
		rwSM.insert(new String("object2"), t2);
		rwSM.insert(new String("object3"), t3);
		
		//Print the initial map
		System.out.println("Status at time " + sm.getGlobalTime() + ":");
		System.out.println(sm);
		System.out.println(rwSM);
		
		//Consecutive tick calculations
		for (int i = 0; i < 3; i++) {
			Arrow acc = new Arrow(1L, 1L, 1L);
			sm.accelerate("object0", acc);
			rwSM.accelerate("object0", acc);
			
			try {
				sm.tick();
			} catch (OverflowException e) {
				System.out.println("StatusMap threw an OverflowException.");
			}
			
			try {
				rwSM.tick();
			} catch (OverflowException e) {
				System.out.println("RWStatusMap threw an OverflowException.");
			}
			
			System.out.println("Status at time " + sm.getGlobalTime() + ":");
			System.out.println(sm);
			System.out.println(rwSM);
		}
		
		//Now two ticks on specific increments
		Arrow acc = new Arrow(2L, 0L, -2L);
		sm.accelerate("object2", acc);
		rwSM.accelerate("object2", acc);
		
		try {
			sm.tick(2);
		} catch (OverflowException e) {
			System.out.println("StatusMap threw an OverflowException.");
		}
		
		try {
			rwSM.tick(2);
		} catch (OverflowException e) {
			System.out.println("RWStatusMap threw an OverflowException.");
		}
		
		System.out.println("Status at time " + sm.getGlobalTime() + ":");
		System.out.println(sm);
		System.out.println(rwSM);
		
		//Test within range on two objects at varying radii
		System.out.println("Objects within range:");
		System.out.println("Objects within range 0:");
		System.out.println(sm.getNearbyObjects(sm.getPosition("object0").getLeft(), 0).getLeft());
		System.out.println("Objects within range 100:");
		System.out.println(sm.getNearbyObjects(sm.getPosition("object0").getLeft(), 100).getLeft());
		System.out.println("Objects within range 500:");
		System.out.println(sm.getNearbyObjects(sm.getPosition("object0").getLeft(), 500).getLeft());
		
		System.out.println("Objects within range 0:");
		System.out.println(sm.getNearbyObjects(sm.getPosition("object2").getLeft(), 0).getLeft());
		System.out.println("Objects within range 100:");
		System.out.println(sm.getNearbyObjects(sm.getPosition("object2").getLeft(), 100).getLeft());
		System.out.println("Objects within range 500:");
		System.out.println(sm.getNearbyObjects(sm.getPosition("object2").getLeft(), 500).getLeft());
		
		//Second incremental ticks
		acc = new Arrow(5L, 0L, -5L);
		sm.accelerate("object3", acc);
		rwSM.accelerate("object3", acc);
		
		try {
			sm.tick(5);
		} catch (OverflowException e) {
			System.out.println("StatusMap threw an OverflowException.");
		}
		
		try {
			rwSM.tick(5);
		} catch (OverflowException e) {
			System.out.println("RWStatusMap threw an OverflowException.");
		}
		
		System.out.println("Status at time " + sm.getGlobalTime() + ":");
		System.out.println(sm);
		System.out.println(rwSM);
		
		//Test within range on two objects at varying radii
		System.out.println("Objects within range:");
		System.out.println("Objects within range 0:");
		System.out.println(sm.getNearbyObjects(sm.getPosition("object1").getLeft(), 0).getLeft());
		System.out.println("Objects within range 100:");
		System.out.println(sm.getNearbyObjects(sm.getPosition("object1").getLeft(), 100).getLeft());
		System.out.println("Objects within range 500:");
		System.out.println(sm.getNearbyObjects(sm.getPosition("object1").getLeft(), 500).getLeft());
		
		System.out.println("Objects within range 0:");
		System.out.println(sm.getNearbyObjects(sm.getPosition("object3").getLeft(), 0).getLeft());
		System.out.println("Objects within range 100:");
		System.out.println(sm.getNearbyObjects(sm.getPosition("object3").getLeft(), 100).getLeft());
		System.out.println("Objects within range 500:");
		System.out.println(sm.getNearbyObjects(sm.getPosition("object3").getLeft(), 500).getLeft());
	}
	
	
}