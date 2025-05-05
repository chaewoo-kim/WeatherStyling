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

        const data = await response.json(); // 응답 데이터를 JSON 형태로 파싱
        return data; // API 정보 반환
    } catch (error) {
        console.error('API 호출 실패:', error);
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

        const data = await response.json(); // 응답 데이터를 JSON 형태로 파싱
        return data; // 추천된 옷 정보 반환
    } catch (error) {
        console.error('API 호출 실패:', error);
        return null; // 에러 발생 시 null 반환 또는 에러 처리
    }
}

/**
 * 추천 API를 호출하고 결과를 화면에 표시하는 함수
 */
async function callRecommendationAPI() {
    const styleSelect = document.getElementById('styleSelect'); // 스타일 선택 select 요소 가져오기
    const sexSelect = document.getElementById('sexSelect'); // 성별 선택 select 요소 가져오기
    const placeNumberSelect = document.getElementById('placeNumberSelect');
    const style = styleSelect.value; // 선택된 스타일 값 가져오기
    const gender = sexSelect.value; // 선택된 성별 값 가져오기
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
        const topCard = document.createElement('div');
        topCard.classList.add('card'); // card 클래스 추가
        topCard.innerHTML = `<img src="${recommendation.top}" alt="상의">`; // 이미지 설정
        cardContainer.appendChild(topCard); // 카드 컨테이너에 카드 추가

        const bottomCard = document.createElement('div');
        bottomCard.classList.add('card');
        bottomCard.innerHTML = `<img src="${recommendation.bottom}" alt="하의">`;
        cardContainer.appendChild(bottomCard);

        const jacketCard = document.createElement('div');
        jacketCard.classList.add('card');
        jacketCard.innerHTML = `<img src="${recommendation.jacket}" alt="재킷">`;
        cardContainer.appendChild(jacketCard);

        const shoesCard = document.createElement('div');
        shoesCard.classList.add('card');
        shoesCard.innerHTML = `<img src="${recommendation.shoes}" alt="신발">`;
        cardContainer.appendChild(shoesCard);
    } else {
        // 추천 결과가 없는 경우
        cardContainer.innerText = '추천된 옷 정보가 없습니다.';
    }
}

/**
 * 로딩끝나면 실행되는 함수
 */
document.addEventListener('DOMContentLoaded', function() {
    const recommendButton = document.getElementById('recommendButton'); // 추천 받기 버튼 요소 가져오기
    if (recommendButton) {
        recommendButton.addEventListener('click', callRecommendationAPI); // 클릭 이벤트 리스너 등록
    }

});