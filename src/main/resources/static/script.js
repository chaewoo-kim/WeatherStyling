// =========================
// 날씨 및 의류 추천 시스템 JS
// =========================

// ✅ 지역 번호(placeNumber)를 기반으로 날씨 정보를 가져오는 비동기 함수
async function getApiInfo(placeNumber) {
    const url = '/api/getApiInfo'; // API 엔드포인트
    const now = new Date(); // 현재 날짜 객체 생성

    // API 요청에 필요한 날짜/시간 데이터 구성
    const requestBody = {
        placeNumber: placeNumber,
        year: now.getFullYear().toString(),
        month: String(now.getMonth() + 1).padStart(2, '0'),
        day: String(now.getDate()).padStart(2, '0'),
        hour: String(now.getHours()).padStart(2, '0'),
        minute: String(now.getMinutes()).padStart(2, '0'),
    };

    // API 호출
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody),
        });

        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return await response.json();
    } catch (error) {
        console.error('getApiInfo 호출 실패:', error);
        return null;
    }
}

// ✅ 스타일과 성별을 기반으로 의상 추천을 받아오는 비동기 함수
async function getRecommendedOutfit(style, gender) {
    const url = '/api/outfit';
    const requestBody = { style, gender };

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody),
        });

        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        return await response.json();
    } catch (error) {
        console.error('outfit 호출 실패:', error);
        return null;
    }
}

// ✅ header 영역에 날씨 정보와 날짜를 표시하는 함수
function updateHeader(apiInfo) {
    const header = document.querySelector('header');
    const h1 = header.querySelector('h1');
    const p = header.querySelector('p');
    const temperatureDiv = header.querySelector('.temperature');

    if (apiInfo) {
        const { TA, HM, RN, WS } = apiInfo;
        h1.innerText = document.getElementById('placeNumberSelect').selectedOptions[0].text;
        p.innerText = `습도: ${HM}%, 강수확률: ${RN}%, 풍속: ${WS}m/s`;
        temperatureDiv.innerText = `${TA}°C`;

        // 날짜 정보 추가
        let dateDiv = header.querySelector('.date');
        if (!dateDiv) {
            dateDiv = document.createElement('div');
            dateDiv.classList.add('date');
            header.appendChild(dateDiv);
        }
        const now = new Date();
        dateDiv.innerText = `날짜: ${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`;
    } else {
        h1.innerText = '지역 정보를 가져올 수 없습니다.';
        p.innerText = 'API 호출에 실패했습니다.';
        temperatureDiv.innerText = '-°C';
    }
}

// ✅ 추천받은 옷 이미지들을 카드 형태로 렌더링하는 함수
function displayRecommendation(recommendation) {
    const cardContainer = document.getElementById('recommendationCardContainer');
    cardContainer.innerHTML = ''; // 기존 카드 초기화

    const items = ['top', 'bottom', 'jacket', 'shoes'];
    items.forEach(item => {
        if (recommendation[item]) {
            const card = document.createElement('div');
            card.classList.add('card');
            card.innerHTML = `<img src="${recommendation[item]}" alt="${item} 이미지">`;
            cardContainer.appendChild(card);
        }
    });
}

// ✅ 다음 추천 조합을 API로 받아서 화면에 표시하는 함수
async function getNextOutfit(style, gender) {
    const recommendation = await getRecommendedOutfit(style, gender);
    if (recommendation) {
        recommendations.push(recommendation);
        currentIndex++;
        displayRecommendation(recommendation);
    }
}

// ✅ 추천 목록과 현재 표시 중인 인덱스를 저장하는 상태 변수들
let recommendations = []; // 추천 저장 배열
let currentIndex = -1; // 현재 추천 인덱스
let style, gender; // 선택된 스타일, 성별

// ✅ DOM 로드 이후 초기 설정 및 버튼 이벤트 등록

document.addEventListener('DOMContentLoaded', () => {
    const weatherButton = document.getElementById('weatherButton');
    const recommendButton = document.getElementById('recommendButton');
    const prevButton = document.getElementById('prevButton');
    const nextButton = document.getElementById('nextButton');

    // 날씨 정보만 불러오는 버튼
    weatherButton?.addEventListener('click', async () => {
        const placeNumber = document.getElementById('placeNumberSelect').value;
        const apiInfo = await getApiInfo(placeNumber);
        updateHeader(apiInfo);
    });

    // 의상 추천 받기 버튼
    recommendButton?.addEventListener('click', async () => {
        style = document.getElementById('styleSelect').value;
        gender = document.getElementById('sexSelect').value;

        const recommendation = await getRecommendedOutfit(style, gender);
        if (recommendation) {
            recommendations.push(recommendation);
            currentIndex = recommendations.length - 1;
            displayRecommendation(recommendation);
        }
    });

    // 이전 추천 보기 버튼
    prevButton?.addEventListener('click', () => {
        if (currentIndex > 0) {
            currentIndex--;
            displayRecommendation(recommendations[currentIndex]);
        }
    });

    // 다음 추천 보기 버튼 (또는 새로운 추천 받기)
    nextButton?.addEventListener('click', async () => {
        if (currentIndex < recommendations.length - 1) {
            currentIndex++;
            displayRecommendation(recommendations[currentIndex]);
        } else {
            await getNextOutfit(style, gender);
        }
    });

    // 현재 요일에 해당하는 라디오 버튼 자동 선택
    const now = new Date();
    const currentDay = now.getDay(); // 일요일: 0
    const dayRadios = document.getElementsByName('day');
    if (currentDay === 0) {
        dayRadios[6].checked = true;
    } else {
        dayRadios[currentDay - 1].checked = true;
    }
});
