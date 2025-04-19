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
 * @param {string} style 스타일 (ex: 캐주얼, 스트릿, 데이트)
 * @param {string} gender 성별 (ex: 여성, 남성)
 * @returns {Promise<object|null>} 추천 의류 정보 (성공 시) 또는 null (실패 시)
 */
async function getRecommendedOutfit(style, gender) {
    const url = '/api/outfit'; // 백엔드 API 엔드포인트
    const requestBody = {
        style: style,
        gender: gender,
    };

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
    const style = styleSelect.value; // 선택된 스타일 값 가져오기
    const gender = sexSelect.value; // 선택된 성별 값 가져오기

    const recommendation = await getRecommendedOutfit(style, gender); // 추천 API 호출

    if (recommendation) {
        console.log('추천된 옷:', recommendation);
        displayRecommendation(recommendation); // 추천 결과 화면에 표시
    } else {
        console.log('추천 실패');
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

    const placeNumberSelect = document.getElementById('placeNumberSelect'); // 지점 번호 선택 select 요소 가져오기
    placeNumberSelect.addEventListener('change', function() {
        const placeNumber = placeNumberSelect.value; // 선택된 지점 번호 가져오기
        getApiInfo(placeNumber); // 지점 번호 변경 시 API 정보 요청
    });
});