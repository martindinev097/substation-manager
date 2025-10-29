document.querySelectorAll('tbody input').forEach(input => {
    input.addEventListener('input', () => {
        const row = input.closest('tr');
        const old = parseFloat(row.querySelector('.old')?.value) || 0;
        const newVal = parseFloat(row.querySelector('.new')?.value) || 0;
        const energyPercentage = parseFloat(row.querySelector('.energy-percentage')?.value) || 0;

        let diff = newVal - old;
        let totalCost = diff * (energyPercentage / 100) * 0.2 / 2;

        if (row.querySelector('.diff')) row.querySelector('.diff').textContent = diff.toFixed(2);
        if (row.querySelector('.total-cost')) row.querySelector('.total-cost').textContent = totalCost.toFixed(2);
    });
});