# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.shortcuts import render
from django.http import JsonResponse

# Create your views here.
def getDrink(self):
	return JsonResponse({'drink':{'title':'arnar','rating':2.5,'description':'lorem ipsum', 'location':'locationLocation'}})
