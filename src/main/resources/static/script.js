// script.js
async function getApiInfo(placeNumber) {
    const url = '/api/getApiInfo'; // 백엔드 API 엔드포인트
    const requestBody = {
        placeNumber: placeNumber,
    };

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestBody),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data; // API 정보 반환
    } catch (error) {
        console.error('API 호출 실패:', error);
        return null; // 에러 발생 시 null 반환 또는 에러 처리
    }
}

async function getRecommendedOutfit(style, sex) {
    const url = '/api/outfit'; // 백엔드 API 엔드포인트
    const requestBody = {
        style: style,
        sex: sex,
    };

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(requestBody),
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        const data = await response.json();
        return data; // 추천된 옷 정보 반환
    } catch (error) {
        console.error('API 호출 실패:', error);
        return null; // 에러 발생 시 null 반환 또는 에러 처리
    }
}

async function callRecommendationAPI() {
    const styleSelect = document.getElementById('styleSelect');
    const sexSelect = document.getElementById('sexSelect');
    const style = styleSelect.value; // 선택된 스타일 값 가져오기
    const sex = sexSelect.value; // 선택된 성별 값 가져오기

    const recommendation = await getRecommendedOutfit(style, sex);

    if (recommendation) {
        console.log('추천된 옷:', recommendation);
        displayRecommendation(recommendation);
    } else {
        console.log('추천 실패');
    }
}

function displayRecommendation(recommendation) {
    const cardContainer = document.getElementById('recommendationCardContainer');
    cardContainer.innerHTML = ''; // 기존 카드 내용 초기화

    if (recommendation) {
        // 추천 결과가 있는 경우
        const topCard = document.createElement('div');
        topCard.classList.add('card');
        topCard.innerHTML = `<img src="${recommendation.top}" alt="상의">`;
        cardContainer.appendChild(topCard);

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

document.addEventListener('DOMContentLoaded', function() {
    const recommendButton = document.getElementById('recommendButton');
    if (recommendButton) {
        recommendButton.addEventListener('click', callRecommendationAPI);
    }

    const placeNumberSelect = document.getElementById('placeNumberSelect');
    placeNumberSelect.addEventListener('change', function() {
        const placeNumber = placeNumberSelect.value; // 선택된 지점 번호 가져오기
        getApiInfo(placeNumber); // 지점 번호 변경 시 API 정보 요청
    });
});
