(() => {
  const links = [
    ['/dashboard.html', 'Dashboard'],
    ['/field-blocks.html', 'Field Blocks'],
    ['/sensors.html', 'Sensors'],
    ['/zones.html', 'Zones'],
    ['/alerts.html', 'Alerts'],
    ['/readings.html', 'Readings'],
    ['/user-management.html', 'Users']
  ];
  function installNav() {
    const existing = document.querySelector('[data-site-nav]');
    if (existing) return;
    const nav = document.createElement('nav');
    nav.dataset.siteNav = 'true';
    nav.setAttribute('aria-label', 'Main navigation');
    nav.innerHTML = links.map(([href, label]) => `<a href="${href}">${label}</a>`).join('');
    const current = location.pathname.endsWith('/') ? '/dashboard.html' : location.pathname;
    nav.querySelectorAll('a').forEach(a => {
      if (a.getAttribute('href') === current) a.classList.add('active');
    });
    const style = document.createElement('style');
    style.textContent = '[data-site-nav]{display:flex;gap:16px;flex-wrap:wrap;background:#163b2c;padding:9px 4%;font-family:Arial,sans-serif}[data-site-nav] a{color:#fff;text-decoration:none;font-size:13px}[data-site-nav] a:hover,[data-site-nav] a.active{text-decoration:underline;font-weight:700}';
    document.head.appendChild(style);
    const header = document.querySelector('header,.header,.head,.navbar');
    if (header) header.insertAdjacentElement('afterend', nav);
    else document.body.prepend(nav);
  }
  if (document.readyState === 'loading') document.addEventListener('DOMContentLoaded', installNav);
  else installNav();
})();
