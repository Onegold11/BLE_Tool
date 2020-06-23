# BLE_Tool

# HM-10 make beacon

beacon은 Advertising 과정에서 31바이트에 해당하는 prefixData 전송
- ibeacon prefix: 9byte
- proximity uuid: 16byte, 사용자 지정
- major: 2bytes, 사용자 지정, 장소 구분
- minor: 2bytes, 사용자 지정, 기기 구분
- tx power: 2bytes

---
+ AT // 응답 확인
+ 명령어 + ? // 해당 변수 값 확인

+ AT+RENEW // 공장초기화 상태로 모듈값 복구
+ AT+NAME[NAME] // 모듈 이름 설정
+ AT+SHOW[0] // 장치 이름 스캔 여부(0: x, 1: o)
+ AT+UUID[0x1000] // 모듈 UUID 설정

+ AT+POWE[3] // 모듈 신호세기 설정(0~3, 기본: 2)
+ AT+ADVI5 //신호 송출 주기를 5로 설정(기본 9, 1.285초, 5: 0.5초)
+ AT+ADTY[3] // Advertising 유형 설정(0, 블루투스 페어링, 1: 마지막 장치 블루투스 페어링, 2: 비콘 모드 + 스캔 응답, 3: 비콘 모드)
		// 전원 절약 및 Advertising Type: 3

+ AT+ROLE[0] // 모듈 역할 설정, Peripheral(slave)(0: peripheral, 1: central)
+ AT+DELO2 //Ibeacon을 BroadCast전용 모드로 세팅(1: 브로드캐스트 + 스캔, 2: 브로드캐스트만)

+ AT+PWRM0 //모듈을 Auto-Sleep모드로 설정(절전효과)(0: auto sleep, 1:don't auto sleep)

+ AT+IBEA[1] // iBeacon Turn on, (0: 비콘 x, 1: 비콘 o)
		// 초기 UUID는 74278BDA-B644-4520-8F0C-720EAF059935
+ AT+RESET // 모듈 재시작
+ AT+IBE(0, 1, 2, 3)[12345678] // 비콘 UUID 값 설정
+ AT+MARJ[0x0009] //비콘의 MAJOR (비콘을 특정GROUP으로 묶을 때 사용)NUMBER설정 (0x1234는 임의 설정 가능)
+ AT+MINO[0x0001] //비콘의 MINOR(GROUP내에 있는 개별 비콘을 구별할 때 사용) NUMBER설정 (0xFA01은 임의 설정 가능)
