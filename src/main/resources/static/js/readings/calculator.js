document.querySelectorAll('tbody input').forEach(input => {
    input.addEventListener('input', () => {
        const row = input.closest('tr');
        const old1 = parseFloat(row.querySelector('.old').value) || 0;
        const new1 = parseFloat(row.querySelector('.new').value) || 0;
        const old2 = parseFloat(row.querySelector('.old2').value) || 0;
        const new2 = parseFloat(row.querySelector('.new2').value) || 0;

        let sumDiffs = (new1 - old1) + (new2 - old2);
        let totalCost = sumDiffs * 0.2 * 13.5 / 100;

        row.querySelector('.diff').textContent = String(new1 - old1);
        row.querySelector('.diff2').textContent = String(new2 - old2);
        row.querySelector('.sum-diffs').textContent = String(sumDiffs);
        row.querySelector('.total-cost').textContent = String(totalCost.toFixed(2));
    });
});