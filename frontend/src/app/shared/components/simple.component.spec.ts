import {TestBed} from '@angular/core/testing';

import {SimpleComponent} from './simple.component';

describe('SimpleComponent', () => {
  it('rend le composant', async () => {
    await TestBed.configureTestingModule({imports: [SimpleComponent]}).compileComponents();

    const fixture = TestBed.createComponent(SimpleComponent);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('Composant partagé prêt.');
  });
});
