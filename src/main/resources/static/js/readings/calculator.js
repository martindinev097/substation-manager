document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll("tbody input").forEach(input => {
        input.addEventListener("input", () => {
            const row = input.closest("tr");
            const old1 = parseFloat(row.querySelector(".old")?.value) || 0;
            const new1 = parseFloat(row.querySelector(".new")?.value) || 0;
            const old2 = parseFloat(row.querySelector(".old2")?.value) || 0;
            const new2 = parseFloat(row.querySelector(".new2")?.value) || 0;

            const diff1 = new1 - old1;
            const diff2 = new2 - old2;
            const sumDiffs = diff1 + diff2;
            const totalCost = (sumDiffs * 0.2 * 13.5) / 100;

            row.querySelector(".diff").value = diff1.toFixed(2);
            row.querySelector(".diff2").value = diff2.toFixed(2);
            row.querySelector(".sum-diffs").value = sumDiffs.toFixed(2);
            row.querySelector(".total-cost").value = totalCost.toFixed(2);
        });
    });
});