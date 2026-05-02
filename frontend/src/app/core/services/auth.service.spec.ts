import {TestBed} from '@angular/core/testing';
import {provideHttpClient} from '@angular/common/http';
import {HttpTestingController, provideHttpClientTesting} from '@angular/common/http/testing';

import {AuthService} from './auth.service';
import {TokenStorageService} from './token-storage.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;
  let tokenStorage: jasmine.SpyObj<TokenStorageService>;

  beforeEach(() => {
    tokenStorage = jasmine.createSpyObj<TokenStorageService>('TokenStorageService', [
      'setTokens',
      'getRefreshToken',
      'clear',
      'getAccessToken'
    ]);

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        {provide: TokenStorageService, useValue: tokenStorage},
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('stocke les tokens après login', () => {
    service.login({email: 'a@b.c', password: 'password123'}).subscribe();

    const req = httpMock.expectOne('http://localhost:8080/api/auth/login');
    expect(req.request.method).toBe('POST');

    req.flush({accessToken: 'token', refreshToken: 'refresh'});
    expect(tokenStorage.setTokens).toHaveBeenCalledWith({accessToken: 'token', refreshToken: 'refresh'});
  });
});
