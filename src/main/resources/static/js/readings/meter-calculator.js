document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll('tbody input').forEach(input => {
        input.addEventListener('input', () => {
            const row = input.closest('tr');
            const old = parseFloat(row.querySelector('.old')?.value) || 0;
            const newVal = parseFloat(row.querySelector('.new')?.value) || 0;
            const energyPercentage = parseFloat(row.querySelector('.energy-percentage')?.value) || 0;

            let diff = newVal - old;
            let totalCost = diff * (energyPercentage / 100) * 0.2 / 2;

            const diffInput = row.querySelector('.diff');
            const totalCostInput = row.querySelector('.total-cost');

            if (diffInput) diffInput.value = diff.toFixed(2);
            if (totalCostInput) totalCostInput.value = totalCost.toFixed(2);
        });
    });
});