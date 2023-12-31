#version 450

/*
 * Copyright 2018 The GraphicsFuzz Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

precision highp float;

layout(location = 0) out vec4 _GLF_color;

uniform float time;

uniform vec2 resolution;

float h_r;
float s_g;
float b_b;

void doConvert()
{
    vec3 temp;
    temp = b_b * (1.0 - s_g) + (b_b - b_b * (1.0 - s_g)) * clamp(abs(abs(6.0 * (h_r - vec3(0, 1, 2) / 3.0)) - 3.0) - 1.0, 0.0, 1.0);
    h_r = temp.x;
    s_g = temp.y;
    b_b = temp.z;
}

vec3 computeColor(float c, vec2 position)
{
    h_r = fract(c);
    s_g = 1.0;
    b_b = (0.5 + (sin(time) * 0.5 + 0.5));
    doConvert();
    s_g *= 1.0 / position.y;
    h_r *= 1.0 / position.x;
    if (abs(position.y - position.x) < 0.5) {
      b_b = clamp(0.0, 1.0, b_b * 3.0);
    }
    return vec3(h_r, s_g, b_b);
}

vec3 defaultColor() {
  return vec3(0.0);
}

vec3 drawShape(vec2 pos, vec2 square, vec3 setting)
{
    bool c1 = pos.x - setting.x < square.x;
    if (!c1) {
      return defaultColor();
    }
    bool c2 = pos.x + setting.x > square.x;
    if (!c2) {
      return defaultColor();
    }
    bool c3 = pos.y - setting.x < square.y;
    if (!c3) {
      return defaultColor();
    }
    bool c4 = pos.y + setting.x > square.y;
    if (!c4) {
      return defaultColor();
    }
    
    bool c5 = pos.x - (setting.x - setting.y) < square.x;
    if (!c5) {
      return computeColor(setting.z / 40.0, pos);
    }
    bool c6 = pos.x + (setting.x - setting.y) > square.x;
    if (!c6) {
      return computeColor(setting.z / 40.0, pos);
    }
    bool c7 = pos.y - (setting.x - setting.y) < square.y;
    if (!c7) {
      return computeColor(setting.z / 40.0, pos);
    }
    bool c8 = pos.y + (setting.x - setting.y) > square.y;
    if (!c8) {
      return computeColor(setting.z / 40.0, pos);
    }
    return defaultColor();
}

vec3 computePoint(mat2 rotationMatrix)
{
    vec2 aspect;
    aspect = resolution.xy / min(resolution.x, resolution.y);
    vec2 position;
    position = (gl_FragCoord.xy / resolution.xy) * aspect;
    vec2 center;
    center = vec2(0.5) * aspect;
    position *= rotationMatrix;
    center *= rotationMatrix;
    vec3 result = vec3(0.0);
    for(int i = 35; i >= 0; i --)
        {
            vec3 d;
            d = drawShape(position, center + vec2(sin(float(i) / 10.0 + time) / 4.0, 0.0), vec3(0.01 + sin(float(i) / 100.0), 0.01, float(i)));
            if(length(d) <= 0.0) {
              continue;
            }
            result = vec3(d);
        }
    return result;
}

void main() {
    float angle;
    angle = sin(time) * 0.1;
    mat2 rotationMatrix;
    rotationMatrix = mat2(sin(angle), - cos(angle), cos(angle), sin(angle));
    vec3 point1;
    point1 = computePoint(rotationMatrix);
    mat2 rotationMatrix2;
    rotationMatrix2 = rotationMatrix * rotationMatrix;
    vec3 point2;
    point2 = computePoint(rotationMatrix2);
    mat2 rotationMatrix3;
    rotationMatrix3 = rotationMatrix * rotationMatrix * rotationMatrix;
    vec3 point3;
    point3 = computePoint(rotationMatrix3);
    vec3 mixed;
    mixed = mix(point1, point2, vec3(0.3));
    mixed = mix(mixed, point3, vec3(0.3));
    _GLF_color = vec4(mixed, 1.0);
}
