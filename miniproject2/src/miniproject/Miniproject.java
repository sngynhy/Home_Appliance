package miniproject;

interface Operation {  // 기기 작동 메뉴얼
	
	public static final int MAX_VOLUME = 10;
	public static final int MIN_VOLUME = 0;
	public static final int MAX_HUMIDITY = 60;  // 실내 적정 습도 40~60%
	public static final int MIN_HUMIDITY = 40;
	public static final int MAX_TEMPERATURE = 26;  // 여름철 실내 적정 온도 26도 / 에어컨 설정 가능 온도 18~26
	public static final int DEFALUT_TEMPERATURE = 20;  // 실내 적정 온도 18~20도
	public static final int MIN_TEMPERATURE = 18; // 겨울철 실내 적정 온도 18~20도
	
	void turnOn();
	void turnOff();
	void autoSetting(int value);  // 자동 작동 value값: 볼륨(TV/Radio), 습도(가습/제습), 온도(에어컨/히터)
	
}

abstract class Home_Appliance implements Operation {  // 가전 제품
	
	String type; // 음향/영상 or 습도조절 or 냉난방
	String name;
	boolean power;  // 전원 on off
	
	@Override
	public void turnOn() {
		System.out.println(this.name + "를 켭니다.");
		this.power = true;
	}
	@Override
	public void turnOff() {
		System.out.println(this.name + "를 끕니다.");
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
	int tmp;  // 음소거 시 원래 볼륨 저장할 변수
	
	RemoteControl() {
		this.type = "음향/영상기기";
	}
	
	abstract void changechannel(int channel); // 채널 or 주파수 변경
	abstract void recording(); // 녹화 or 녹음 기능
	
	
	// 볼륨 조절 기능
	// 만약 볼륨 입력값이 최저~최고범위를 벗어나면 최저/최고볼륨으로 자동 설정
	// 범위 내의 값이면 입력값으로 볼륨 설정
	@Override
	public void autoSetting(int volume) {  // 볼륨 조절 기능
		if (!this.power) {
			System.out.println("전원이 꺼져있는 상태입니다.");
			return;
		}
		else if (volume > Operation.MAX_VOLUME) {
			this.volume = Operation.MAX_VOLUME;
			System.out.println("설정 가능한 최고 볼륨은 " + Operation.MAX_VOLUME + "입니다.");
		}
		else if (volume < Operation.MIN_VOLUME) {
			this.volume = Operation.MIN_VOLUME;
			System.out.println("설정 가능한 최저 볼륨은 " + Operation.MIN_VOLUME + "입니다.");
		}
		else {
			this.volume = volume;
		}
		this.tmp = this.volume;
		System.out.println("현재 볼륨은 " + this.volume + "입니다.");
	}
	
	// 음소거 기능
	public void setMute(boolean mute) {
		if (mute) {
			System.out.println("음소거 처리합니다.");
			this.volume = 0;
		} else {
			System.out.println("음소거 해제합니다.");
			this.volume = this.tmp;  // 볼륨 원상태로
		}
	}
	
}

abstract class HumidityControl extends Home_Appliance {
	
	int humidity;
	int water; // 현재 물의 양

	HumidityControl(int water) {
		this.water = water;
		this.type = "습도조절기기";
	}
	
	abstract void manualSetting(int water);  // 습도 수동 조절

}

abstract class TemperatureControl extends Home_Appliance {
	
	int temperature;
	TemperatureControl() {
		this.type = "냉난방기기";
	}

	abstract void manualSetting(int temperature);  // 온도 수동 조절

}
	
//////////////////////////////////////////////////////

class TV extends RemoteControl {
	
	TV() {
		super();
		this.name = "TV";
	}
	
	@Override
	public void changechannel(int channel) { // 채널 변경
		if (!this.power) {
			System.out.println("전원이 꺼져있는 상태입니다.");
			return;
		}
		this.channel = channel;
		System.out.println("채널 " + this.channel + "번으로 변경!");
	}
	@Override
	public void recording() { // 녹화
		if (!this.power) {
			System.out.println("전원이 꺼져있는 상태입니다.");
			return;
		}
		System.out.println("TV 녹화를 시작합니다.");
	}

}
class Radio extends RemoteControl {
	
	Radio() {
		super();
		this.name = "Radio";
	}
	
	@Override
	public void changechannel(int channel) { // 주파수 변경
		if (!this.power) {
			System.out.println("전원이 꺼져있는 상태입니다.");
			return;
		}
		this.channel = channel;
		System.out.println("주파수 " + this.channel + "Hz로 변경!");
	}
	@Override
	public void recording() { // 녹음
		if (!this.power) {
			System.out.println("전원이 꺼져있는 상태입니다.");
			return;
		}
		System.out.println("Radio 녹음을 시작합니다.");
	}

}

//////////////////////////////////////////////////////

class Humidifier extends HumidityControl {
	
	Humidifier(int water) {
		super(water);
		this.name = "가습기";
	}
	
	// 자동 습도 조절
	// 실내 적정 습도 범위 40~60% 를 벗어나면 자동으로 습도를 조절하여 유지
	// 가습기 사용은 건조할 때 이므로 습도가 40% 아래로 떨어지면 60%가 유지되도록
	@Override
	public void autoSetting(int humidity) {
		this.humidity = humidity;
		if (!this.power) {
			System.out.println("전원이 꺼져있는 상태입니다.");
			return;
		}
		else if (humidity < Operation.MIN_HUMIDITY) {  // 현재 습도 < 40%이면 60%로 맞추기
			System.out.println("가습기가 Auto 모드로 작동합니다.");
			boolean flag = true;
			do {
				this.humidity++;
				this.water--;
				System.out.print(this.humidity + "% "); // 실시간 습도 확인
				// 습도가 60%가 되거나 물의 양이 0이 되면 동작을 멈추도록
				if(this.humidity == Operation.MAX_HUMIDITY || this.water == 0) {
					flag = false;
				}
			} while (flag);
			System.out.println();
			if (this.humidity == Operation.MAX_HUMIDITY) {
				System.out.println("현재 습도는 "+ Operation.MAX_HUMIDITY +"% 입니다. Auto 모드를 종료합니다.");
			}
		}
		if (this.water == 0) {
			System.out.println("삐!삐! 물이 없습니다. 물을 추가해주세요.");
		}
	}
	
	// 수동 조절
	// 가습기 내 물의 양이 0이거나 습도가 100%가 될때까지 실행
	@Override
	public void manualSetting(int water) {   // 물의 양과 반비례
		this.water = water;
		if (!this.power) {
			System.out.println("전원이 꺼져있는 상태입니다.");
			return;
		}
		else if (this.water > 0) {
			System.out.println("[현재 습도>> " + this.humidity + "%  물의 양>> " + this.water + "]");
			System.out.println("가습기가 작동합니다.");
			boolean flag = true;
			do {
				this.humidity++;
				this.water--;
				System.out.print(this.humidity + "% "); // 실시간 습도 확인
				if (this.water == 0 || this.humidity == 100) {  // 물이 모두 사용되거나 습도가 100이면 동작을 멈추도록
					flag = false;
				}
			} while (flag);
		}
		System.out.println();
		if (this.water == 0) {
			System.out.println("삐!삐! 물이 없습니다. 물을 추가해주세요.");
		}
		System.out.println("현재 습도는 " + this.humidity + "% 입니다.");
	}
	
}
class Dehumidifier extends HumidityControl {
	
	Dehumidifier(int water) {
		super(water);
		this.name = "제습기";
	}
	
	// 자동 습도 조절
	// 실내 적정 습도 범위 40~60% 를 벗어나면 자동으로 습도를 조절하여 유지
	// 제습기 사용은 습할 때 이므로 습도가 60% 위로 올라가면 40%가 유지되도록
	@Override
	public void autoSetting(int humidity) { 
		this.humidity = humidity;
		if (!this.power) {
			System.out.println("전원이 꺼져있는 상태입니다.");
			return;
		}
		else if (humidity > Operation.MAX_HUMIDITY) { // 현재 습도 < 40%
			System.out.println("제습기가 Auto 모드로 작동합니다.");
			boolean flag = true;
			do {
				this.humidity--;
				this.water++;
				System.out.print(this.humidity + "% "); // 실시간 습도 확인
				if (this.water == 100 || this.humidity == Operation.MIN_HUMIDITY) {  // 물이 가득 차거나 습도가 40이면 동작을 멈추도록
					flag = false;
				}
			} while (flag);  // 현재 습도 == 60% 될 때까지 실행
			System.out.println();
			if (this.humidity == Operation.MIN_HUMIDITY) {
				System.out.println("현재 습도는 "+ Operation.MIN_HUMIDITY +"% 입니다. Auto 모드를 종료합니다.");
			}
		}
		if (this.water == 100) {
			System.out.println("삐!삐! 물이 가득 찼습니다. 물을 비워주세요.");
		}
	}
	
	// 수동 조절
	// 제습기 내 물의 양이 100이거나 습도가 0%가 될때까지 실행
	@Override
	public void manualSetting(int water) { // 물의 양과 반비례
		this.water = water;
		if (!this.power) {
			System.out.println("전원이 꺼져있는 상태입니다.");
			return;
		}
		else if (this.water < 100) {
			System.out.println("[현재 습도>> " + this.humidity + "%  물의 양>> " + this.water + "]");
			System.out.println("제습기가 작동합니다.");
			boolean flag = true;
			do {
				this.humidity--;
				this.water++;
				System.out.print(this.humidity + "% "); // 실시간 습도 확인
				if (this.water == 100 || this.humidity == 0) {  // 물이 100으로 가득 차거나 습도가 0이면 동작을 멈추도록
					flag = false;
				}
			} while (flag);
		}
		System.out.println();
		if (this.water == 100) {
			System.out.println("삐!삐! 물이 가득 찼습니다. 물을 비워주세요.");
		}
		System.out.println("현재 습도는 " + this.humidity + "% 입니다.");
	}
}

//////////////////////////////////////////////////////

class AirConditioner extends TemperatureControl {
	
	AirConditioner() {
		super();
		this.name = "에어컨";
	}
	
	// 자동 온도 조절 - 여름철 실내 적정 온도 26도에 맞추기
	// 현재 온도(입력값)가 26도가 아니면 실행
	@Override
	public void autoSetting(int temperature) {
		if (!this.power) {
			System.out.println("전원이 꺼져있는 상태입니다.");
			return;
		} else if (temperature > 26) {  // 온도 > 26이면 26도로 자동 설정
			this.temperature = temperature;
			System.out.println("에어컨이 Auto 모드로 작동합니다.");
			System.out.println("여름철 적정 실내 온도는 26˚C 입니다. 실내 온도를 26˚C로 유지합니다.");
			do {
				this.temperature--;
				System.out.print(this.temperature + "˚C "); // 실시간 온도 확인
			} while (this.temperature > Operation.MAX_TEMPERATURE);  // 26도
		} else if (temperature < 26) {
			this.temperature = temperature;
			System.out.println("에어컨이 Auto 모드로 작동합니다.");
			System.out.println("여름철 적정 실내 온도는 26˚C 입니다. 실내 온도를 26˚C로 유지합니다.");
			do {
				this.temperature++;
				System.out.print(this.temperature + "˚C "); // 실시간 온도 확인
			} while (this.temperature < Operation.MAX_TEMPERATURE);  // 26도
		}
		System.out.println();
		System.out.println("현재 온도는 " + this.temperature + "˚C 입니다.");
	}
	
	// 수동 조절
	// 에어컨 설정 가능 온도 18~26
	// 만약 희망온도 입력값이 최저~최고범위를 벗어나면 최저/최고 온도로 자동 설정
	// 범위 내의 값이면 입력값으로 온도 설정
	@Override
	public void manualSetting(int temperature) {
		if (!this.power) {
			System.out.println("전원이 꺼져있는 상태입니다.");
			return;
		}
		else if (temperature > Operation.MAX_TEMPERATURE) { // 설정하고자 하는 온도가 에어컨 최고기온보다 높으면
			this.temperature = Operation.MAX_TEMPERATURE; // 26도로 설정
			System.out.println("설정 가능한 최고 온도는 " + Operation.MAX_TEMPERATURE +"˚C 입니다.");
		} else if (temperature < Operation.MIN_TEMPERATURE) {
			this.temperature = Operation.MIN_TEMPERATURE; // 26도로 설정
			System.out.println("설정 가능한 최저 온도는 " + Operation.MIN_TEMPERATURE +"˚C 입니다.");
		} else {
			this.temperature = temperature;
		}
		System.out.println("희망 온도를 " + this.temperature + "˚C로 설정합니다.");
			
	}
		
}

class Heater extends TemperatureControl {
	
	Heater() {
		super();
		this.name = "히터";
	}
	
	// 겨울철 실내 적정 온도 20도에 맞추기 (18~20도)
	// 현재 온도(입력값)가 20도가 아니면 실행
	@Override
	public void autoSetting(int temperature) {
		if (!this.power) {
			System.out.println("전원이 꺼져있는 상태입니다.");
			return;
		} else if (temperature < 20) {
			this.temperature = temperature;
			System.out.println("히터가 Auto 모드로 작동합니다.");
			System.out.println("겨울철 적정 실내 온도는 20˚C 입니다. 실내 온도를 20˚C로 유지합니다.");
			do {
				this.temperature++;
				System.out.print(this.temperature + "˚C "); // 실시간 온도 확인
			} while (this.temperature < Operation.DEFALUT_TEMPERATURE);  // 20도로 설정
		} else if (temperature > 20) {
			this.temperature = temperature;
			System.out.println("히터가 Auto 모드로 작동합니다.");
			System.out.println("겨울철 적정 실내 온도는 20˚C 입니다. 실내 온도를 20˚C로 유지합니다.");
			do {
				this.temperature--;
				System.out.print(this.temperature + "˚C "); // 실시간 온도 확인
			} while (this.temperature > Operation.DEFALUT_TEMPERATURE);  // 20도로 설정
		}
		System.out.println();
		System.out.println("현재 온도는 " + this.temperature + "˚C 입니다.");
	}
	
	// 수동 - 히터 설정 가능 온도 18~26
	// 히터 설정 가능 온도 18~26
	// 만약 희망온도 입력값이 최저~최고범위를 벗어나면 최저/최고 온도로 자동 설정
	// 범위 내의 값이면 입력값으로 온도 설정
	@Override
	public void manualSetting(int temperature) {
		if (!this.power) {
			System.out.println("전원이 꺼져있는 상태입니다.");
			return;
		}
		else if (temperature > Operation.MAX_TEMPERATURE) { // 설정하고자 하는 온도가 에어컨 최고기온보다 높으면
			this.temperature = Operation.MAX_TEMPERATURE; // 26도로 설정
			System.out.println("설정 가능한 최고 온도는 " + Operation.MAX_TEMPERATURE +"˚C 입니다.");
		} else if (temperature < Operation.MIN_TEMPERATURE) {
			this.temperature = Operation.MIN_TEMPERATURE; // 18도로 설정
			System.out.println("설정 가능한 최저 온도는 " + Operation.MIN_TEMPERATURE +"˚C 입니다.");
		} else {
			this.temperature = temperature;
		}
		System.out.println("희망 온도를 " + this.temperature + "˚C로 설정합니다.");
		
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
		System.out.println("현재볼륨: " + rd.volume);
		rd.setMute(false);
		System.out.println("현재볼륨: " + rd.volume);
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
