document.addEventListener('DOMContentLoaded', function() {
    document.querySelectorAll('.dropdown > a').forEach(a => {
        a.addEventListener('click', e => {
            e.preventDefault();

            document.querySelectorAll('.dropdown.open').forEach(d => {
                if (d !== a.parentElement) d.classList.remove('open');
            });

            a.parentElement.classList.toggle('open');
        });
    });
});