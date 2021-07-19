package miniproject;

interface Operation {  // ��� �۵� �޴���
	
	public static final int MAX_VOLUME = 10;
	public static final int MIN_VOLUME = 0;
	public static final int MAX_HUMIDITY = 60;  // �ǳ� ���� ���� 40~60%
	public static final int MIN_HUMIDITY = 40;
	public static final int MAX_TEMPERATURE = 26;  // ����ö �ǳ� ���� �µ� 26�� / ������ ���� ���� �µ� 18~26
	public static final int DEFALUT_TEMPERATURE = 20;  // �ǳ� ���� �µ� 18~20��
	public static final int MIN_TEMPERATURE = 18; // �ܿ�ö �ǳ� ���� �µ� 18~20��
	
	void turnOn();
	void turnOff();
	void autoSetting(int value);  // �ڵ� �۵� value��: ����(TV/Radio), ����(����/����), �µ�(������/����)
	
}

abstract class Home_Appliance implements Operation {  // ���� ��ǰ
	
	String type; // ����/���� or �������� or �ó���
	String name;
	boolean power;  // ���� on off
	
	@Override
	public void turnOn() {
		System.out.println(this.name + "�� �մϴ�.");
		this.power = true;
	}
	@Override
	public void turnOff() {
		System.out.println(this.name + "�� ���ϴ�.");
		this.power = false;
	}
	
	@Override
		public String toString() {
			return "[" + this.type + "] " + this.name;
	}
}
//////////////////////////////////////////////////////
// 

abstract class RemoteControl extends Home_Appliance {
	
	int channel;
	int volume;
	int tmp;  // ���Ұ� �� ���� ���� ������ ����
	
	RemoteControl() {
		this.type = "����/������";
	}
	
	abstract void changechannel(int channel); // ä�� or ���ļ� ����
	abstract void recording(); // ��ȭ or ���� ���
	
	
	// ���� ���� ���
	// ���� ���� �Է°��� ����~�ְ������ ����� ����/�ְ������� �ڵ� ����
	// ���� ���� ���̸� �Է°����� ���� ����
	@Override
	public void autoSetting(int volume) {  // ���� ���� ���
		if (!this.power) {
			System.out.println("������ �����ִ� �����Դϴ�.");
			return;
		}
		else if (volume > Operation.MAX_VOLUME) {
			this.volume = Operation.MAX_VOLUME;
			System.out.println("���� ������ �ְ� ������ " + Operation.MAX_VOLUME + "�Դϴ�.");
		}
		else if (volume < Operation.MIN_VOLUME) {
			this.volume = Operation.MIN_VOLUME;
			System.out.println("���� ������ ���� ������ " + Operation.MIN_VOLUME + "�Դϴ�.");
		}
		else {
			this.volume = volume;
		}
		this.tmp = this.volume;
		System.out.println("���� ������ " + this.volume + "�Դϴ�.");
	}
	
	// ���Ұ� ���
	public void setMute(boolean mute) {
		if (mute) {
			System.out.println("���Ұ� ó���մϴ�.");
			this.volume = 0;
		} else {
			System.out.println("���Ұ� �����մϴ�.");
			this.volume = this.tmp;  // ���� �����·�
		}
	}
	
}

abstract class HumidityControl extends Home_Appliance {
	
	int humidity;
	int water; // ���� ���� ��

	HumidityControl(int water) {
		this.water = water;
		this.type = "�����������";
	}
	
	abstract void manualSetting(int water);  // ���� ���� ����

}

abstract class TemperatureControl extends Home_Appliance {
	
	int temperature;
	TemperatureControl() {
		this.type = "�ó�����";
	}

	abstract void manualSetting(int temperature);  // �µ� ���� ����

}
	
//////////////////////////////////////////////////////

class TV extends RemoteControl {
	
	TV() {
		super();
		this.name = "TV";
	}
	
	@Override
	public void changechannel(int channel) { // ä�� ����
		if (!this.power) {
			System.out.println("������ �����ִ� �����Դϴ�.");
			return;
		}
		this.channel = channel;
		System.out.println("ä�� " + this.channel + "������ ����!");
	}
	@Override
	public void recording() { // ��ȭ
		if (!this.power) {
			System.out.println("������ �����ִ� �����Դϴ�.");
			return;
		}
		System.out.println("TV ��ȭ�� �����մϴ�.");
	}

}
class Radio extends RemoteControl {
	
	Radio() {
		super();
		this.name = "Radio";
	}
	
	@Override
	public void changechannel(int channel) { // ���ļ� ����
		if (!this.power) {
			System.out.println("������ �����ִ� �����Դϴ�.");
			return;
		}
		this.channel = channel;
		System.out.println("���ļ� " + this.channel + "Hz�� ����!");
	}
	@Override
	public void recording() { // ����
		if (!this.power) {
			System.out.println("������ �����ִ� �����Դϴ�.");
			return;
		}
		System.out.println("Radio ������ �����մϴ�.");
	}

}

//////////////////////////////////////////////////////

class Humidifier extends HumidityControl {
	
	Humidifier(int water) {
		super(water);
		this.name = "������";
	}
	
	// �ڵ� ���� ����
	// �ǳ� ���� ���� ���� 40~60% �� ����� �ڵ����� ������ �����Ͽ� ����
	// ������ ����� ������ �� �̹Ƿ� ������ 40% �Ʒ��� �������� 60%�� �����ǵ���
	@Override
	public void autoSetting(int humidity) {
		this.humidity = humidity;
		if (!this.power) {
			System.out.println("������ �����ִ� �����Դϴ�.");
			return;
		}
		else if (humidity < Operation.MIN_HUMIDITY) {  // ���� ���� < 40%�̸� 60%�� ���߱�
			System.out.println("�����Ⱑ Auto ���� �۵��մϴ�.");
			boolean flag = true;
			do {
				this.humidity++;
				this.water--;
				System.out.print(this.humidity + "% "); // �ǽð� ���� Ȯ��
				// ������ 60%�� �ǰų� ���� ���� 0�� �Ǹ� ������ ���ߵ���
				if(this.humidity == Operation.MAX_HUMIDITY || this.water == 0) {
					flag = false;
				}
			} while (flag);
			System.out.println();
			if (this.humidity == Operation.MAX_HUMIDITY) {
				System.out.println("���� ������ "+ Operation.MAX_HUMIDITY +"% �Դϴ�. Auto ��带 �����մϴ�.");
			}
		}
		if (this.water == 0) {
			System.out.println("��!��! ���� �����ϴ�. ���� �߰����ּ���.");
		}
	}
	
	// ���� ����
	// ������ �� ���� ���� 0�̰ų� ������ 100%�� �ɶ����� ����
	@Override
	public void manualSetting(int water) {   // ���� ��� �ݺ��
		this.water = water;
		if (!this.power) {
			System.out.println("������ �����ִ� �����Դϴ�.");
			return;
		}
		else if (this.water > 0) {
			System.out.println("[���� ����>> " + this.humidity + "%  ���� ��>> " + this.water + "]");
			System.out.println("�����Ⱑ �۵��մϴ�.");
			boolean flag = true;
			do {
				this.humidity++;
				this.water--;
				System.out.print(this.humidity + "% "); // �ǽð� ���� Ȯ��
				if (this.water == 0 || this.humidity == 100) {  // ���� ��� ���ǰų� ������ 100�̸� ������ ���ߵ���
					flag = false;
				}
			} while (flag);
		}
		System.out.println();
		if (this.water == 0) {
			System.out.println("��!��! ���� �����ϴ�. ���� �߰����ּ���.");
		}
		System.out.println("���� ������ " + this.humidity + "% �Դϴ�.");
	}
	
}
class Dehumidifier extends HumidityControl {
	
	Dehumidifier(int water) {
		super(water);
		this.name = "������";
	}
	
	// �ڵ� ���� ����
	// �ǳ� ���� ���� ���� 40~60% �� ����� �ڵ����� ������ �����Ͽ� ����
	// ������ ����� ���� �� �̹Ƿ� ������ 60% ���� �ö󰡸� 40%�� �����ǵ���
	@Override
	public void autoSetting(int humidity) { 
		this.humidity = humidity;
		if (!this.power) {
			System.out.println("������ �����ִ� �����Դϴ�.");
			return;
		}
		else if (humidity > Operation.MAX_HUMIDITY) { // ���� ���� < 40%
			System.out.println("�����Ⱑ Auto ���� �۵��մϴ�.");
			boolean flag = true;
			do {
				this.humidity--;
				this.water++;
				System.out.print(this.humidity + "% "); // �ǽð� ���� Ȯ��
				if (this.water == 100 || this.humidity == Operation.MIN_HUMIDITY) {  // ���� ���� ���ų� ������ 40�̸� ������ ���ߵ���
					flag = false;
				}
			} while (flag);  // ���� ���� == 60% �� ������ ����
			System.out.println();
			if (this.humidity == Operation.MIN_HUMIDITY) {
				System.out.println("���� ������ "+ Operation.MIN_HUMIDITY +"% �Դϴ�. Auto ��带 �����մϴ�.");
			}
		}
		if (this.water == 100) {
			System.out.println("��!��! ���� ���� á���ϴ�. ���� ����ּ���.");
		}
	}
	
	// ���� ����
	// ������ �� ���� ���� 100�̰ų� ������ 0%�� �ɶ����� ����
	@Override
	public void manualSetting(int water) { // ���� ��� �ݺ��
		this.water = water;
		if (!this.power) {
			System.out.println("������ �����ִ� �����Դϴ�.");
			return;
		}
		else if (this.water < 100) {
			System.out.println("[���� ����>> " + this.humidity + "%  ���� ��>> " + this.water + "]");
			System.out.println("�����Ⱑ �۵��մϴ�.");
			boolean flag = true;
			do {
				this.humidity--;
				this.water++;
				System.out.print(this.humidity + "% "); // �ǽð� ���� Ȯ��
				if (this.water == 100 || this.humidity == 0) {  // ���� 100���� ���� ���ų� ������ 0�̸� ������ ���ߵ���
					flag = false;
				}
			} while (flag);
		}
		System.out.println();
		if (this.water == 100) {
			System.out.println("��!��! ���� ���� á���ϴ�. ���� ����ּ���.");
		}
		System.out.println("���� ������ " + this.humidity + "% �Դϴ�.");
	}
}

//////////////////////////////////////////////////////

class AirConditioner extends TemperatureControl {
	
	AirConditioner() {
		super();
		this.name = "������";
	}
	
	// �ڵ� �µ� ���� - ����ö �ǳ� ���� �µ� 26���� ���߱�
	// ���� �µ�(�Է°�)�� 26���� �ƴϸ� ����
	@Override
	public void autoSetting(int temperature) {
		if (!this.power) {
			System.out.println("������ �����ִ� �����Դϴ�.");
			return;
		} else if (temperature > 26) {  // �µ� > 26�̸� 26���� �ڵ� ����
			this.temperature = temperature;
			System.out.println("�������� Auto ���� �۵��մϴ�.");
			System.out.println("����ö ���� �ǳ� �µ��� 26��C �Դϴ�. �ǳ� �µ��� 26��C�� �����մϴ�.");
			do {
				this.temperature--;
				System.out.print(this.temperature + "��C "); // �ǽð� �µ� Ȯ��
			} while (this.temperature > Operation.MAX_TEMPERATURE);  // 26��
		} else if (temperature < 26) {
			this.temperature = temperature;
			System.out.println("�������� Auto ���� �۵��մϴ�.");
			System.out.println("����ö ���� �ǳ� �µ��� 26��C �Դϴ�. �ǳ� �µ��� 26��C�� �����մϴ�.");
			do {
				this.temperature++;
				System.out.print(this.temperature + "��C "); // �ǽð� �µ� Ȯ��
			} while (this.temperature < Operation.MAX_TEMPERATURE);  // 26��
		}
		System.out.println();
		System.out.println("���� �µ��� " + this.temperature + "��C �Դϴ�.");
	}
	
	// ���� ����
	// ������ ���� ���� �µ� 18~26
	// ���� ����µ� �Է°��� ����~�ְ������ ����� ����/�ְ� �µ��� �ڵ� ����
	// ���� ���� ���̸� �Է°����� �µ� ����
	@Override
	public void manualSetting(int temperature) {
		if (!this.power) {
			System.out.println("������ �����ִ� �����Դϴ�.");
			return;
		}
		else if (temperature > Operation.MAX_TEMPERATURE) { // �����ϰ��� �ϴ� �µ��� ������ �ְ��º��� ������
			this.temperature = Operation.MAX_TEMPERATURE; // 26���� ����
			System.out.println("���� ������ �ְ� �µ��� " + Operation.MAX_TEMPERATURE +"��C �Դϴ�.");
		} else if (temperature < Operation.MIN_TEMPERATURE) {
			this.temperature = Operation.MIN_TEMPERATURE; // 26���� ����
			System.out.println("���� ������ ���� �µ��� " + Operation.MIN_TEMPERATURE +"��C �Դϴ�.");
		} else {
			this.temperature = temperature;
		}
		System.out.println("��� �µ��� " + this.temperature + "��C�� �����մϴ�.");
			
	}
		
}

class Heater extends TemperatureControl {
	
	Heater() {
		super();
		this.name = "����";
	}
	
	// �ܿ�ö �ǳ� ���� �µ� 20���� ���߱� (18~20��)
	// ���� �µ�(�Է°�)�� 20���� �ƴϸ� ����
	@Override
	public void autoSetting(int temperature) {
		if (!this.power) {
			System.out.println("������ �����ִ� �����Դϴ�.");
			return;
		} else if (temperature < 20) {
			this.temperature = temperature;
			System.out.println("���Ͱ� Auto ���� �۵��մϴ�.");
			System.out.println("�ܿ�ö ���� �ǳ� �µ��� 20��C �Դϴ�. �ǳ� �µ��� 20��C�� �����մϴ�.");
			do {
				this.temperature++;
				System.out.print(this.temperature + "��C "); // �ǽð� �µ� Ȯ��
			} while (this.temperature < Operation.DEFALUT_TEMPERATURE);  // 20���� ����
		} else if (temperature > 20) {
			this.temperature = temperature;
			System.out.println("���Ͱ� Auto ���� �۵��մϴ�.");
			System.out.println("�ܿ�ö ���� �ǳ� �µ��� 20��C �Դϴ�. �ǳ� �µ��� 20��C�� �����մϴ�.");
			do {
				this.temperature--;
				System.out.print(this.temperature + "��C "); // �ǽð� �µ� Ȯ��
			} while (this.temperature > Operation.DEFALUT_TEMPERATURE);  // 20���� ����
		}
		System.out.println();
		System.out.println("���� �µ��� " + this.temperature + "��C �Դϴ�.");
	}
	
	// ���� - ���� ���� ���� �µ� 18~26
	// ���� ���� ���� �µ� 18~26
	// ���� ����µ� �Է°��� ����~�ְ������ ����� ����/�ְ� �µ��� �ڵ� ����
	// ���� ���� ���̸� �Է°����� �µ� ����
	@Override
	public void manualSetting(int temperature) {
		if (!this.power) {
			System.out.println("������ �����ִ� �����Դϴ�.");
			return;
		}
		else if (temperature > Operation.MAX_TEMPERATURE) { // �����ϰ��� �ϴ� �µ��� ������ �ְ��º��� ������
			this.temperature = Operation.MAX_TEMPERATURE; // 26���� ����
			System.out.println("���� ������ �ְ� �µ��� " + Operation.MAX_TEMPERATURE +"��C �Դϴ�.");
		} else if (temperature < Operation.MIN_TEMPERATURE) {
			this.temperature = Operation.MIN_TEMPERATURE; // 18���� ����
			System.out.println("���� ������ ���� �µ��� " + Operation.MIN_TEMPERATURE +"��C �Դϴ�.");
		} else {
			this.temperature = temperature;
		}
		System.out.println("��� �µ��� " + this.temperature + "��C�� �����մϴ�.");
		
	}
}

//////////////////////////////////////////////////////

public class Miniproject {
	
	public static void main(String[] args) {
		
		TV tv = new TV();
		Radio rd = new Radio();
		Humidifier hd = new Humidifier(50);
		Dehumidifier dhd = new Dehumidifier(50);
		AirConditioner ac = new AirConditioner();
		Heater ht = new Heater();
		
		Home_Appliance[] data = new Home_Appliance[6];
		data[0] = tv;
		data[1] = rd;
		data[2] = hd;
		data[3] = dhd;
		data[4] = ac;
		data[5] = ht;
		
		for (Home_Appliance ha : data) {
			System.out.println(ha);
		}
		
		System.out.println();
		
		tv.turnOn();
		tv.changechannel(10);
		tv.autoSetting(20);
		tv.recording();
		tv.turnOff();
		
		System.out.println();
		
		rd.turnOn();
		rd.changechannel(108);
		rd.autoSetting(7);
		rd.setMute(true);
		System.out.println("���纼��: " + rd.volume);
		rd.setMute(false);
		System.out.println("���纼��: " + rd.volume);
		rd.recording();
		rd.turnOff();
		
		System.out.println();
		
		hd.turnOn();
		hd.autoSetting(35);
		hd.manualSetting(10);;
		hd.turnOff();
		
		System.out.println();
		
		dhd.turnOn();
		dhd.autoSetting(80);
		dhd.manualSetting(40);
		dhd.turnOff();
		
		System.out.println();
		
		ac.turnOn();
		ac.autoSetting(24);
		ac.autoSetting(30);
		ac.manualSetting(17);
		ac.manualSetting(20);
		ac.turnOff();
		
		System.out.println();
		
		ht.turnOn();
		ht.autoSetting(18);
		ht.autoSetting(26);
		ht.manualSetting(17);
		ht.manualSetting(28);
		ht.turnOff();

		
	}
}
