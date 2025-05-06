/**
 * 특정 지역 번호에 대한 API 정보를 가져오는 함수
 * @param {string} placeNumber 지역 번호
 * @returns {Promise<object|null>} API 정보 (성공 시) 또는 null (실패 시)
 */
async function getApiInfo(placeNumber) {
    const url = '/api/getApiInfo'; // 백엔드 API 엔드포인트

    const now = new Date(); // 현재 날짜 및 시간 가져오기
    const year = now.getFullYear().toString(); // (4자리)
    const month = String(now.getMonth() + 1).padStart(2, '0'); // (2자리, 0으로 채움)
    const day = String(now.getDate()).padStart(2, '0');
    const hour = String(now.getHours()).padStart(2, '0');
    const minute = String(now.getMinutes()).padStart(2, '0');

    const requestBody = {
        placeNumber: placeNumber,
        year: year,
        month: month,
        day: day,
        hour: hour,
        minute: minute,
    };

    try {
        console.log(url);
        const response = await fetch(url, {
            method: 'POST', // POST 요청 사용
            headers: {
                'Content-Type': 'application/json', // JSON 형식으로 데이터 전송
            },
            body: JSON.stringify(requestBody), // 요청 body에 JSON 데이터 담기
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`); // HTTP 에러 발생 시 에러 던지기
        }

        return await response.json(); // 응답 데이터를 JSON 형태로 파싱 후 API 정보 반환
    } catch (error) {
        console.error('getApiInfo 호출 실패:', error);
        return null; // 에러 발생 시 null 반환 또는 에러 처리
    }
}

/**
 * 스타일과 성별에 따른 추천 의류 정보를 가져오는 함수
 * @param {string} style 스타일 (ex: 캐주얼, 미니멀, 포멀)
 * @param {string} gender 성별 (ex: 여성, 남성)
 * @returns {Promise<object|null>} 추천 의류 정보 (성공 시) 또는 null (실패 시)
 */
async function getRecommendedOutfit(style, gender) {
    const url = '/api/outfit'; // 백엔드 API 엔드포인트
    const requestBody = {
        style: style,
        gender: gender,
    };
    console.log(url);
    try {
        const response = await fetch(url, {
            method: 'POST', // POST 요청 사용
            headers: {
                'Content-Type': 'application/json', // JSON 형식으로 데이터 전송
            },
            body: JSON.stringify(requestBody), // 요청 body에 JSON 데이터 담기
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`); // HTTP 에러 발생 시 에러 던지기
        }

        return await response.json(); // 응답 데이터를 JSON 형태로 파싱 추천된 옷 정보 반환
    } catch (error) {
        console.error('outfit 호출 실패:', error);
        return null; // 에러 발생 시 null 반환 또는 에러 처리
    }
}

/**
 * 추천 API를 호출하고 결과를 화면에 표시하는 함수
 */
let recommendations = []; // 추천 결과를 저장할 배열
let currentIndex = -1; // 현재 표시 중인 조합의 인덱스
let style, gender;
async function callRecommendationAPI() {
    const styleSelect = document.getElementById('styleSelect'); // 스타일 선택 select 요소 가져오기
    const sexSelect = document.getElementById('sexSelect'); // 성별 선택 select 요소 가져오기
    const placeNumberSelect = document.getElementById('placeNumberSelect');
    style = styleSelect.value; // 선택된 스타일 값 가져오기
    gender = sexSelect.value; // 선택된 성별 값 가져오기
    const placeNumber = placeNumberSelect.value;

    const apiInfo = await getApiInfo(placeNumber);

    if (apiInfo) {
        console.log('여기까진 정상 작동');
        console.log('API 정보:', apiInfo);
        // API 정보를 사용하여 header 업데이트
        updateHeader(apiInfo);
    } else {
        console.log('API 정보 가져오기 실패');
    }

    const recommendation = await getRecommendedOutfit(style, gender); // 추천 API 호출

    if (recommendation) {
        console.log('추천된 옷:', recommendation);
        recommendations.push(recommendation); // 추천 결과를 배열에 추가
        currentIndex = recommendations.length - 1; // 현재 인덱스를 마지막으로 설정
        displayRecommendation(recommendation); // 추천 결과 화면에 표시
    } else {
        console.log('추천 실패');
    }
}

/**
 * API 정보를 받아 헤더를 업데이트하는 함수
 * @param {object} apiInfo API 정보 (온도, 습도, 강수확률, 풍속 속성 포함)
 */
function updateHeader(apiInfo) {
    const header = document.querySelector('header'); // header 요소 가져오기
    const h1 = header.querySelector('h1'); // h1 요소 가져오기
    const p = header.querySelector('p'); // p 요소 가져오기
    const temperatureDiv = header.querySelector('.temperature'); // temperature div 요소 가져오기
    console.log(Object.keys(apiInfo).length === 0);
    if (apiInfo) {
        // API 정보가 있는 경우
        const { HM, RN, WS } = apiInfo; // API 정보에서 값 추출
        const TA = apiInfo.TA
        console.log(TA, HM, RN, WS);
        // header 내용 업데이트
        h1.innerText = document.getElementById('placeNumberSelect').options[document.getElementById('placeNumberSelect').selectedIndex].text; // 지역 이름으로 업데이트
        p.innerText = `습도: ${HM}%, 강수확률: ${RN}%, 풍속: ${WS}m/s`; // 습도, 강수확률, 풍속 표시
        temperatureDiv.innerText = `${TA}°C`; // 온도 표시
    } else {
        // API 정보가 없는 경우
        h1.innerText = '지역 정보를 가져올 수 없습니다.';
        p.innerText = 'API 호출에 실패했습니다.';
        temperatureDiv.innerText = '-°C';
    }
}

/**
 * 추천 결과를 카드 형태로 화면에 표시하는 함수
 * @param {object} recommendation 추천 의류 정보 (jacket, top, bottom, shoes 속성 포함)
 */
function displayRecommendation(recommendation) {
    const cardContainer = document.getElementById('recommendationCardContainer'); // 카드 컨테이너 요소 가져오기
    cardContainer.innerHTML = ''; // 기존 카드 내용 초기화

    if (recommendation) {
        // 추천 결과가 있는 경우
        const { top, bottom, jacket, shoes } = recommendation;
        const topCard = document.createElement('div');
        topCard.classList.add('card'); // card 클래스 추가
        topCard.innerHTML = `<img src="${top}" alt="상의">`; // 이미지 설정
        cardContainer.appendChild(topCard); // 카드 컨테이너에 카드 추가

        const bottomCard = document.createElement('div');
        bottomCard.classList.add('card');
        bottomCard.innerHTML = `<img src="${bottom}" alt="하의">`;
        cardContainer.appendChild(bottomCard);

        const jacketCard = document.createElement('div');
        jacketCard.classList.add('card');
        jacketCard.innerHTML = `<img src="${jacket}" alt="재킷">`;
        cardContainer.appendChild(jacketCard);

        const shoesCard = document.createElement('div');
        shoesCard.classList.add('card');
        shoesCard.innerHTML = `<img src="${shoes}" alt="신발">`;
        cardContainer.appendChild(shoesCard);
    } else {
        // 추천 결과가 없는 경우
        cardContainer.innerText = '추천된 옷 정보가 없습니다.';
    }
}

async function getNextOutfit(style, gender) {
    const recommendation = await getRecommendedOutfit(style, gender); // 새로운 조합을 가져오는 API 호출
    if (recommendation) {
        recommendations.push(recommendation); // 새로운 조합을 배열에 추가
        currentIndex++; // 인덱스를 증가
        displayRecommendation(recommendation); // 추천 결과 화면에 표시
    } else {
        console.log('추천 실패');
    }
}


document.addEventListener('DOMContentLoaded', function() {
    const recommendButton = document.getElementById('recommendButton'); // 추천 받기 버튼 요소 가져오기
    const prevButton = document.getElementById('prevButton'); // 왼쪽 화살표 버튼
    const nextButton = document.getElementById('nextButton'); // 오른쪽 화살표 버튼

    if (recommendButton) {
        recommendButton.addEventListener('click', callRecommendationAPI); // 추천 받기 버튼 클릭 이벤트
    }

    if (prevButton) {
        prevButton.addEventListener('click', () => {
            console.log('왼쪽 화살표 클릭됨');
            console.log('현재 인덱스 (클릭 전):', currentIndex);

            if (currentIndex > 0) {
                currentIndex--;
                displayRecommendation(recommendations[currentIndex]);
                console.log('핸재 인덱스 (클릭 후):', currentIndex);
            } else {
                console.log('첫 번째 조합입니다.');
            }
        });
    }

    if (nextButton) {
        nextButton.addEventListener('click', async () => {
            console.log('오른쪽 화살표 클릭됨');
            console.log('현재 인덱스 (클릭 전):', currentIndex);

            if (currentIndex < recommendations.length - 1) {
                currentIndex++;
                displayRecommendation(recommendations[currentIndex]);
                console.log('현재 인덱스 (클릭 후):', currentIndex);
            } else {
                await getNextOutfit(style, gender);
            }
        });
    }
});

document.addEventListener('DOMContentLoaded', function() {
    // 현재 요일을 가져옵니다.
    const now = new Date();
    const currentDay = now.getDay(); // 0: 일요일, 1: 월요일, ..., 6: 토요일

    const dayRadios = document.getElementsByName('day');

    if (currentDay === 0) {
        dayRadios[6].checked = true; // 일요일
    } else {
        dayRadios[currentDay - 1].checked = true; // 현재 요일에 해당하는 라디오 버튼 선택
    }
});