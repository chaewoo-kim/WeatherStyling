/**
 * 특정 지역 번호에 대한 API 정보를 가져오는 함수
 * @param {string} placeNumber 지역 번호
 * @param {string} year
 * @param {string} month
 * @param {string} day
 * @param {string} hour
 * @param {string} minute
 * @param {string} regCode (선택)
 * @returns {Promise<object|null>} API 정보 (성공 시) 또는 null (실패 시)
 */
async function getApiInfo(placeNumber, year, month, day, hour, minute, regCode) {
    const url = '/api/getApiInfo'; // 백엔드 API 엔드포인트
    const requestBody = {
        placeNumber: placeNumber,
        year: year,
        month: month,
        day: day,
        hour: hour,
        minute: minute
    };
    // regCode가 있으면 추가
    if (regCode) requestBody.reg = regCode;

    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody),
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('getApiInfo 호출 실패:', error);
        return null;
    }
}

async function getRecommendedOutfit(style, gender) {
    const url = '/api/outfit';
    const requestBody = { style, gender };
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(requestBody),
        });
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return await response.json();
    } catch (error) {
        console.error('outfit 호출 실패:', error);
        return null;
    }
}

let recommendations = [];
let currentIndex = -1;
let style, gender;

const dayValueMap = {"sun": 0, "mon": 1, "tue": 2, "wed": 3, "thu": 4, "fri": 5, "sat": 6};

async function callRecommendationAPI() {
    const styleSelect = document.getElementById('styleSelect');
    const sexSelect = document.getElementById('sexSelect');
    const placeNumberSelect = document.getElementById('placeNumberSelect');
    const selectedOption = placeNumberSelect.options[placeNumberSelect.selectedIndex];
    const placeNumber = selectedOption.value;
    const regCode = selectedOption.getAttribute('data-reg'); // ★ 추가: reg 코드 추출

    style = styleSelect.value;
    gender = sexSelect.value;

    recommendations = [];
    currentIndex = -1;

    const selectedDayRadio = document.querySelector('input[name="day"]:checked');
    const selectedDayValue = selectedDayRadio ? selectedDayRadio.value : null;

    let targetDate = new Date();
    if (selectedDayValue) {
        const currentDayOfWeek = targetDate.getDay();
        const selectedDayOfWeek = dayValueMap[selectedDayValue];
        if (selectedDayOfWeek !== undefined) {
            let diff = selectedDayOfWeek - currentDayOfWeek;
            if (diff < 0) diff += 7;
            targetDate.setDate(targetDate.getDate() + diff);
        }
    }

    const year = targetDate.getFullYear().toString();
    const month = String(targetDate.getMonth() + 1).padStart(2, '0');
    const day = String(targetDate.getDate()).padStart(2, '0');
    const hour = '09';
    const minute = '00';

    // ★ regCode까지 포함해서 전달
    const apiInfo = await getApiInfo(placeNumber, year, month, day, hour, minute, regCode);

    if (apiInfo) {
        updateHeader(apiInfo);
    }

    const recommendation = await getRecommendedOutfit(style, gender);

    if (recommendation) {
        recommendations.push(recommendation);
        currentIndex = recommendations.length - 1;
        displayRecommendation(recommendation);
    }
}

function updateHeader(apiInfo) {
    const header = document.querySelector('header');
    const h1 = header.querySelector('h1');
    const p = header.querySelector('p');
    const temperatureDiv = header.querySelector('.temperature');
    if (apiInfo) {
        const { TA, ST, SKY, PREP } = apiInfo;
        let weatherCondition = '';
        if (PREP === '0') {
            switch (SKY) {
                case 'DB01': weatherCondition = '맑음'; break;
                case 'DB02': weatherCondition = '구름 조금'; break;
                case 'DB03': weatherCondition = '구름 많음'; break;
                case 'DB04': weatherCondition = '흐림'; break;
            }
        } else {
            switch (PREP) {
                case '1': weatherCondition = '비'; break;
                case '2': weatherCondition = '비/눈'; break;
                case '3': weatherCondition = '눈'; break;
                case '4': weatherCondition = '눈/비'; break;
            }
        }
        h1.innerText = document.getElementById('placeNumberSelect').options[
            document.getElementById('placeNumberSelect').selectedIndex].text;
        p.innerText = `강수확률: ${ST}%`;
        temperatureDiv.innerText = `${weatherCondition}, ${TA}°C`;
    } else {
        h1.innerText = '지역 정보를 가져올 수 없습니다.';
        p.innerText = 'API 호출에 실패했습니다.';
        temperatureDiv.innerText = '-°C';
    }
}

async function removeBgImage(imageUrl) {
    const apiKey = 'H4Zdg6njJ9Z6NQHfAu8XgYL7';
    const endpoint = 'https://api.remove.bg/v1.0/removebg';
    const formData = new FormData();
    formData.append('image_url', imageUrl);
    formData.append('size', 'auto');
    try {
        const response = await fetch(endpoint, {
            method: 'POST',
            headers: { 'X-Api-Key': apiKey },
            body: formData
        });
        if (!response.ok) throw new Error('remove.bg 호출 실패');
        const blob = await response.blob();
        return URL.createObjectURL(blob);
    } catch (err) {
        console.error(err);
        return imageUrl;
    }
}

async function displayRecommendation(recommendation) {
    const cardContainer = document.getElementById('recommendationCardContainer');
    cardContainer.innerHTML = '';
    if (recommendation) {
        const { top, bottom, jacket, shoes } = recommendation;
        const items = [
            { src: top, alt: '상의' },
            { src: bottom, alt: '하의' },
            { src: jacket, alt: '재킷' },
            { src: shoes, alt: '신발' }
        ];
        const promises = items.map(item => removeBgImage(item.src));
        const bgRemovedUrls = await Promise.all(promises);
        items.forEach((item, idx) => {
            const card = document.createElement('div');
            card.classList.add('card');
            card.innerHTML = `<img src="${bgRemovedUrls[idx]}" alt="${item.alt}">`;
            cardContainer.appendChild(card);
        });
    }
}

async function getNextOutfit(style, gender) {
    const recommendation = await getRecommendedOutfit(style, gender);
    if (recommendation) {
        recommendations.push(recommendation);
        currentIndex++;
        displayRecommendation(recommendation);
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const recommendButton = document.getElementById('recommendButton');
    const prevButton = document.getElementById('prevButton');
    const nextButton = document.getElementById('nextButton');
    if (recommendButton) {
        recommendButton.addEventListener('click', callRecommendationAPI);
    }
    if (prevButton) {
        prevButton.addEventListener('click', () => {
            if (currentIndex > 0) {
                currentIndex--;
                displayRecommendation(recommendations[currentIndex]);
            }
        });
    }
    if (nextButton) {
        nextButton.addEventListener('click', async () => {
            if (currentIndex < recommendations.length - 1) {
                currentIndex++;
                displayRecommendation(recommendations[currentIndex]);
            } else {
                await getNextOutfit(style, gender);
            }
        });
    }
    // 오늘 요일 자동 선택
    const now = new Date();
    const currentDay = now.getDay();
    const dayRadios = document.getElementsByName('day');
    if (currentDay === 0) dayRadios[6].checked = true;
    else dayRadios[currentDay - 1].checked = true;
});