/**
 * 수량 [−] [+] 버튼 동작
 *
 * HTML 구조:
 * <div class="qty-stepper">
 *     <button type="button" class="qty-btn" data-step="-1">−</button>
 *     <input type="number" class="qty-input" min="1" max="12" value="1">
 *     <button type="button" class="qty-btn" data-step="1">+</button>
 * </div>
 *
 * data-step="-1" → 1 줄이기, data-step="1" → 1 늘리기
 * 숫자는 input의 min(최소) ~ max(최대, 재고) 사이에서만 움직여요.
 */

// 수량칸의 최소값, 최대값 꺼내기
function getRange(input) {
    const min = parseInt(input.min || '1', 10);
    const max = input.max ? parseInt(input.max, 10) : Infinity; // max가 없으면 끝없이
    return { min, max };
}

// 현재 숫자에 맞춰 [−] [+] 버튼을 켜거나 끄기
// 예) 1이면 [−] 끄기, 재고와 같으면 [+] 끄기
function updateButtons(stepper) {
    const input = stepper.querySelector('.qty-input');
    const minusButton = stepper.querySelector('.qty-btn[data-step="-1"]');
    const plusButton = stepper.querySelector('.qty-btn[data-step="1"]');
    const { min, max } = getRange(input);
    const value = parseInt(input.value, 10);

    minusButton.disabled = isNaN(value) || value <= min;
    plusButton.disabled = isNaN(value) || value >= max;
}

// [−] [+] 버튼을 눌렀을 때
// 버튼마다 따로 연결하지 않고, 화면 전체에서 클릭을 한 번에 받아서 처리해요 (이벤트 위임)
document.addEventListener('click', function (event) {
    const button = event.target.closest('.qty-btn');
    if (!button) {
        return; // 수량 버튼이 아니면 무시
    }

    const stepper = button.closest('.qty-stepper');
    const input = stepper.querySelector('.qty-input');
    const { min, max } = getRange(input);
    const step = parseInt(button.dataset.step, 10); // -1 또는 1

    let value = parseInt(input.value, 10);
    if (isNaN(value)) {
        value = min; // 칸이 비어 있으면 최소값부터 시작
    }

    // 1 더하거나 빼고, min ~ max 범위를 넘지 않게 자르기
    value = Math.min(Math.max(value + step, min), max);
    input.value = value;

    updateButtons(stepper);
});

// 숫자를 직접 입력했을 때도 버튼 상태 맞추기
document.addEventListener('input', function (event) {
    if (event.target.classList.contains('qty-input')) {
        updateButtons(event.target.closest('.qty-stepper'));
    }
});

// 화면이 처음 열릴 때 모든 수량칸의 버튼 상태 맞추기
document.addEventListener('DOMContentLoaded', function () {
    document.querySelectorAll('.qty-stepper').forEach(updateButtons);
});