package root;

import java.util.ArrayList;

public class BinaryHeap {
	public class Entry{
		int x, y, dir;
		int cost;
		Entry(int x_, int y_, int dir_, int cost_) {x = x_;y = y_;dir = dir_;cost = cost_;}
	}
	ArrayList<Entry> data;
	
	BinaryHeap() {
		data = new ArrayList<Entry>();
	}
	
	private void siftUp(int pos) {
		Entry a, b;
		if(pos == 0) {return;}
		a = data.get(pos);
		b = data.get((pos - 1) / 2);
		if(a.cost < b.cost) {
			data.set(pos, b);
			data.set((pos - 1) / 2, a);
			siftUp((pos - 1) / 2);
		} else {
			return;
		}
	}
	
	private void siftDown(int pos) {
		Entry a, b, c;
		if(pos * 2 + 1 >= data.size()) {return;}
		a = data.get(pos);
		b = data.get(pos * 2 + 1);
		if(pos * 2 + 2 < data.size()) {
			c = data.get(pos * 2 + 2);
		} else {
			if(a.cost > b.cost) {
				data.set(pos, b);
				data.set(pos * 2 + 1, a);
				siftDown(pos * 2 + 1);
				return;
			} else {
				return;
			}
		}
		if(a.cost < b.cost) {
			if(c.cost < a.cost) {
				data.set(pos, c);
				data.set(pos * 2 + 2, a);
				siftDown(pos * 2 + 2);
			} else {
				return;
			}
		} else {
			if(b.cost < c.cost) {
				data.set(pos, b);
				data.set(pos * 2 + 1, a);
				siftDown(pos * 2 + 1);
			} else {
				data.set(pos, c);
				data.set(pos * 2 + 2, a);
				siftDown(pos * 2 + 2);
			}
		}
	}
	
	public void insert(int x, int y, int dir, int heur) {
		data.add(new Entry(x, y, dir, heur));
		int pos = data.size() - 1;
		siftUp(pos);
	}
	
	public Entry pop() {
		if(data.size() == 0) {return null;}
		Entry result = data.get(0);
		Entry ent = data.get(data.size() - 1);
		data.set(0, ent);
		data.remove(data.size() - 1);
		siftDown(0);
		return result;
	}
	
	public Entry minkey() {
		if(data.size() == 0) {return null;} else {return data.get(0);}
	}
}
