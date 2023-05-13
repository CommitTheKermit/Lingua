- 프로젝트 개요
- 개발 목적
- 셀프 피드백
- 기능 구현
- 설치 방법 및 주요 기능 실행 방법
- 커밋 히스토리

## **프로젝트 개요**
    앱 이름 : Lingua (링구아)
    프로젝트 핵심 기능 : 영어 원서 번역 도움 및 뷰어 
    참여자 : 우정현
    단위 : 개인 프로젝트
    제작 기간: 
        프로토타입 : 2022.11. ~ 2022.12.
        추가 수정  : 2023.05 ~ 
    주요 기능: 문장 단위 기계번역, 단어 검색 기능
    사용 기술 및 도구: Android Studio Java, openAI API, Oxford API
    문의: commit3921@gmail.com

## **개발 목적**
     게임 Captain Bones를 번역하면서 느낀 어려움과 영국 소설 Brothers of Snake, Severed를 
    읽으며 영어를 어느정도 알아도 번역하는 것이 어렵다는 것을 느껴 그것을 편하게 번역하고 싶다는 마음에 프로젝트를 시작하게 되었습니다.

## **셀프 피드백**
    - 파일 탐색기 ✔️ 0510
    - openAI machine translate ✔️ 0512
    - save a index that last written ✔️ 0513
    - api key 암호화 ❔ 0514 
    - 단어 검색 기록 이용하여 단어장 만들기
    - 임의 단어 사전 검색 기능 (TextView로 사전에 검색 요청 보내기)
    - if next sentence starts with quotation mark pop next sentence and append to current sentence.
    - application life cycle onPause or onDestory save the current index number and load last saved index number
    - make original text ui bigger, translated text ui smaller
    - if next sentence is equal to "'." extend that sentence to former sentence
    - what about adding slide window?
    - checkbox for machine translate
    - Django, MariaDB, MongoDB, AWS EC2\
    - pdf to txt 변환 만들기
    

## **기능 구현**
<br>

### 메인 화면
<img width="50%" alt="메인 화면" src="https://github.com/CommitTheKermit/Lingua/assets/113445660/4adcf987-fa18-4b05-b8d7-51fbd0309d4f"/>
<br>

### 단어 풀이 및 번역
<img width="50%" alt="메인 화면" src="https://github.com/CommitTheKermit/Lingua/assets/113445660/1b26622c-b4b9-4a11-94ca-1b259d77ccef"/>

## **설치 방법 및 주요 기능 실행 방법**
    1. Releases에 있는 .apk 파일을 안드로이드 휴대폰으로 이동 후 설치
    2. /storage/emulated/0/Download 경로에 본 레포지토리 Text 폴더에 있는 Severed.txt 파일 옮기기
    3. 어플리케이션 실행
    4. 왼쪽 위 메뉴 버튼을 눌러 [파일 읽기]를 눌러 Severed.txt 파일을 선택
    5. 이전줄, 다음줄 버튼을 눌러 줄 단위 이동 가능
    6. 왼쪽 위 메뉴 버튼을 눌러 [줄 이동]을 눌러 임의 줄 이동 가능
    7. 오른쪽 위 책 모양 버튼을 눌러 문단 단위 열람 및 이동 가능
    8. 아래 단어들을 눌러 단어의 의미 열람 가능
        8-1. 번역하기 버튼을 눌러 의미들을 번역 가능

## **커밋 히스토리**
[레포지토리 커밋 히스토리](https://github.com/CommitTheKermit/Lingua/commits/main)



