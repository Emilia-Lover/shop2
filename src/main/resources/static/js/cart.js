/**
 * 장바구니 체크박스 동작
 *  - 맨 위 체크박스: 전체 선택 / 전체 해제
 *  - 체크할 때마다 "선택 N개 상품", "결제 예정 금액"을 다시 계산
 *  - 하나도 선택하지 않으면 [선택 상품 주문] 버튼 비활성화
 */
document.addEventListener('DOMContentLoaded', function () {
    const checkAll = document.getElementById('check-all');
    if (!checkAll) {
        return; // 장바구니가 비어 있으면 할 일이 없어요
    }

    const checks = document.querySelectorAll('.cart-check');
    const countText = document.getElementById('selected-count');
    const totalText = document.getElementById('selected-total');
    const orderButton = document.getElementById('order-selected-btn');

    // 선택한 개수와 금액 다시 계산
    function refresh() {
        let count = 0;
        let total = 0;
        checks.forEach(function (check) {
            if (check.checked) {
                count++;
                total += Number(check.dataset.total); // data-total 값
            }
        });

        countText.textContent = count;
        totalText.textContent = total.toLocaleString('ko-KR') + '원'; // 1290000 → 1,290,000원
        orderButton.disabled = (count === 0);
        checkAll.checked = (count === checks.length); // 하나라도 빠지면 전체 선택 해제
    }

    // 전체 선택 체크박스를 누르면 모두 같이 체크/해제
    checkAll.addEventListener('change', function () {
        checks.forEach(function (check) {
            check.checked = checkAll.checked;
        });
        refresh();
    });

    // 각 체크박스를 누를 때마다 다시 계산
    checks.forEach(function (check) {
        check.addEventListener('change', refresh);
    });

    refresh();
});